let stompClient = null;
let isSubscribed = false;
let lastSystemMessage = null;
let chatBox = document.getElementById('chat-box');
let messageInput = document.getElementById('messageInput');
let sendBtn = document.getElementById('sendBtn');


document.addEventListener("DOMContentLoaded",async ()=>{
    let roomId = "";
    const detailDto = document.querySelector("#chat1-1-detail-dto");
    // if(!detailDto)
    //     return;
    const loginEmail = detailDto.dataset.loginEmail;
    const receiverId = Number(detailDto.dataset.receiverId);
    roomId = Number(detailDto.dataset.roomId);


    const token = getJwtToken();
    //console.log(data);

    if(!roomId && receiverId)
        roomId = Number(await getPrivateChatRoomId(receiverId));

    // 채팅방 참여자인지 체크
    await checkIsRoomParticipant(roomId);

    // SockJs 설정
    setStompClient();
    //메시지 보낼 때 설정
    setSend(roomId);

    //연결 해제 설정
    setDisconnects(roomId);

    // 연결 시도
    await connect(token, roomId, loginEmail);
    console.log("STOMP 연결 성공");

    // 채팅 기록
    await getChatHistory(roomId, loginEmail);


})

function getJwtToken(){
    const tokenCookie = document.cookie
        .split('; ')
        .find(cookie => cookie.startsWith('Authorization='));

    return tokenCookie ? tokenCookie.split('=')[1] : null;
}

function setStompClient() {
    const sock = new SockJS(`/connect`);
    stompClient = webstomp.over(sock);
}

function connect(token, roomId, loginEmail) {
    if(stompClient && stompClient.connected)
        return; // 중복 연결 방지

    return new Promise((resolve, reject) => {
        stompClient.connect(
            { Authorization: `Bearer ${token}` },
            () => {
                // ✅ 연결 성공
                if(isSubscribed&&stompClient.connected)
                    return;
                console.log("STOMP 연결 성공");
                stompClient.subscribe(`/topic/${roomId}`, message => {
                    const body = JSON.parse(message.body);
                    appendMessage(body, loginEmail);
                    scrollToBottom();

                    //console.error("메시지 처리 중 에러", e);
                }, { Authorization: `Bearer ${token}` });
                isSubscribed = true;
                resolve(); // 성공 시
            },
            (error) => {
                console.error("STOMP 연결 실패");
                showErrorPage(error?.headers?.message || "Chat STOMP 연결 실패");
                //reject(error); // 실패 시
            }
        );
    });
}


function setSend(roomId) {
    // 3. 메시지 전송
    sendBtn.addEventListener('click', ()=>{
        sendMessage(roomId);
    });
    messageInput.addEventListener('keyup', (e) => {
        if (e.key === 'Enter') sendMessage(roomId);
    });
}


function setDisconnects(roomId) {
// 4. 나가기 전 disconnect
    window.addEventListener('beforeunload', () => {
        // fetch(`/chat/room/${roomId}/read`, { method: "POST" });
        // if (stompClient && stompClient.connected) {
        //     stompClient.unsubscribe(`/topic/${roomId}`);
        //     stompClient.disconnect();
        // }
        const data = new Blob([JSON.stringify({ roomId })], { type: 'application/json' });
        navigator.sendBeacon(`/chat/room/read?roomId=${roomId}`, data);

        if (stompClient && stompClient.connected) {
            stompClient.disconnect(); // 이건 백그라운드 전송 못함 → 실패할 수 있음
        }

    });

    // document.addEventListener('visibilitychange', () => {
    //     if (document.visibilityState === 'hidden') {
    //         fetch(`/chat/room/read?roomId=${roomId}`, { method: "POST", credentials:"include" });
    //         if (stompClient && stompClient.connected) {
    //             stompClient.unsubscribe(`/topic/${roomId}`);
    //             stompClient.disconnect();
    //
    //         }
    //     }
    // });

    window.addEventListener('pagehide', () => {
        fetch(`/chat/room/read?roomId=${roomId}`, { method: "POST", credentials:"include" });
        if (stompClient && stompClient.connected) {
            stompClient.unsubscribe(`/topic/${roomId}`);
            stompClient.disconnect();
        }
    });
}


function sendMessage(roomId) {
    const msgText = messageInput.value.trim();
    if (!msgText) return;

    const content = {
        content: msgText
    };

    stompClient.send(`/publish/${roomId}`, JSON.stringify(content));
    messageInput.value = '';
}

function appendMessage(msg, loginEmail) {

    if(msg.exceptionName === "NotChatParticipantException")
        showErrorPage(msg.content);

    console.log(msg);

    const isSystemMsg = msg.exceptionName === "ChatBlockException";
    const isDuplicateSystemMsg = isSystemMsg && lastSystemMessage === msg.content;

    if (isSystemMsg && !isDuplicateSystemMsg) {
        appendSystemMessage("⚠️ 상대방에게 메시지를 보낼 수 없습니다.");
        lastSystemMessage = msg.content;
        return;
    }
    if(!isSystemMsg){
        const msgDiv = document.createElement('div');
        msgDiv.className = 'chat-message ' + (msg.senderEmail === loginEmail ? 'sent' : 'received');
        const time = new Date(msg.createdDate).toLocaleString('ko-KR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });

        msgDiv.innerHTML = `
                <strong>${msg.senderName}:</strong><br/>
                ${msg.content}
                <span class="chat-timestamp">${time}</span>
                `;
        chatBox.appendChild(msgDiv);
        lastSystemMessage = null;
    }
}

function appendSystemMessage(content){

    const msgDiv = document.createElement('div');
    msgDiv.className = 'chat-message system-message';
    msgDiv.textContent = content;
    chatBox.appendChild(msgDiv);
    scrollToBottom();
}


function scrollToBottom() {
    chatBox.scrollTop = chatBox.scrollHeight;
}



async function getPrivateChatRoomId(receiverId){
    try{
        const response = await fetch(`/chat/room/private?receiverId=${receiverId}`,{
            method: "GET",
            credentials: "include"
        });
        if(!response.ok){
            const data = await response.json();
            // console.log(data);
            // console.log(data.error);

            showErrorPage(data.error);
        }
        const data = await response.json();

        return data.data;
    }catch (err){
        showErrorPage("현재 채팅방 참여자가 아닙니다.");
    }




}

async function checkIsRoomParticipant(roomId) {
    try{
        const response = await fetch(`/chat/check/my/rooms?roomId=${roomId}`,{
            method: "GET",
            credentials: "include"
        })

        if(!response.ok){
            const data = await response.json();
            showErrorPage(data.error);
        }
    }catch (err){
        console.error(err);
        showErrorPage("현재 채팅방 참여자가 아닙니다.");
    }

}

async function getChatHistory(roomId, loginEmail) {
    try{
        const response = await fetch(`/chat/history?roomId=${roomId}`,{
            method: "GET",
            credentials: "include"
        });
        if(!response.ok){
            const data = await response.json();
            // console.log(data);
            // console.log(data.error);
            //showErrorPage(data.error);
        }
        const data = await response.json();

        data.data.forEach(dto =>{
            appendMessage(dto, loginEmail);
            scrollToBottom();
        })
    }catch(err){
        console.error("가져올 이전 기록이 없습니다.");
    }



}

function showErrorPage(error){
    const form  = document.createElement("form");
    form.method = "POST";
    form.action = "/error/chat";

    const input = document.createElement("input");
    input.type = "hidden";
    input.name = "error";
    input.value = error||"Chat Error 발생";
    form.appendChild(input);
    document.body.appendChild(form);
    form.submit();
}


