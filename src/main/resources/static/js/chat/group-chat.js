let stompClient = null;
let isSubscribed = false;
let lastSystemMessage = null;
let chatBox = document.getElementById('chat-box');
let messageInput = document.getElementById('messageInput');
let sendBtn = document.getElementById('sendBtn');
let loginEmail = '';
let roomId = '';

document.addEventListener("DOMContentLoaded",async ()=>{

    const detailDto = document.querySelector("#group-chat-detail-dto");

    loginEmail = detailDto.dataset.loginEmail;
    roomId = Number(detailDto.dataset.roomId);

    const token = getJwtToken();
    //console.log(data);

    // if(!roomId)
    //     roomId = Number(await getPrivateChatRoomId(receiverId));

    // 채팅방 참여자인지 체크
    await checkIsRoomParticipant();

    // SockJs 설정
    setStompClient();
    //메시지 보낼 때 설정
    setSend();
    //연결 해제 설정
    setDisconnects();

    // 연결 시도
    await connect(token);
    console.log("STOMP 연결 성공");

    // 채팅 기록
    await getChatHistory();

    setSidePanel();

    initParticipantList();

    document.querySelector(".refresh-list").addEventListener("click",async ()=>{
        await initParticipantList();
    });





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

function connect(token) {
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

                stompClient.subscribe(`/user/${loginEmail}/queue/errors`,(message)=>{

                    const data = new Blob([JSON.stringify({ roomId })], { type: 'application/json' });
                    navigator.sendBeacon(`/chat/room/read?roomId=${roomId}`, data);

                    if (stompClient && stompClient.connected) {
                        stompClient.disconnect(); // 이건 백그라운드 전송 못함 → 실패할 수 있음
                    }
                    const form  = document.createElement("form");
                    form.method = "POST";
                    form.action = "/error/chat";

                    const input = document.createElement("input");
                    input.type = "hidden";
                    input.name = "error";
                    input.value = "Chat 더 이상 그룹 채팅할 수 없습니다.";
                    form.appendChild(input);
                    document.body.appendChild(form);
                    form.submit();

                })
                resolve(); // 성공 시
            },
            (error) => {
                console.error("STOMP 연결 실패");
                showErrorPage(error?.headers?.message || "Chat STOMP 연결 실패");
                reject(error); // 실패 시
            }
        );
    });
}


function setSend() {
    // 3. 메시지 전송
    sendBtn.addEventListener('click', ()=>{
        sendMessage(roomId);
    });
    messageInput.addEventListener('keyup', (e) => {
        if (e.key === 'Enter') sendMessage(roomId);
    });
}


function setDisconnects() {
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
    //         fetch(`/chat/room/read?roomId=${roomId}`, { method: "POST",credentials:"include" });
    //         if (stompClient && stompClient.connected) {
    //             stompClient.unsubscribe(`/topic/${roomId}`);
    //             stompClient.disconnect();
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


function sendMessage() {
    const msgText = messageInput.value.trim();
    if (!msgText) return;

    const content = {
        content: msgText
    };

    try{
        stompClient.send(`/publish/${roomId}`, JSON.stringify(content));
    }catch (e){
        //alert("메시지 전송에 실패했습니다.");
        Swal.fire({text: '메시지 전송에 실패했습니다.', icon: 'warning', confirmButtonText: '확인'});
    }

    messageInput.value = '';
}

function appendMessage(msg) {
    if(msg.exceptionName === "NotChatParticipantException")
        showErrorPage(msg.content);

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

function appendSystemMessage(content) {

    const msgDiv = document.createElement('div');
    msgDiv.className = 'chat-message system-message';
    msgDiv.textContent = content;
    chatBox.appendChild(msgDiv);
    scrollToBottom();
}


function scrollToBottom() {
    chatBox.scrollTop = chatBox.scrollHeight;
}



// async function getPrivateChatRoomId(receiverId){
//     try{
//         const response = await fetch(`/chat/room/private?receiverId=${receiverId}`,{
//             method: "GET",
//             credentials: "include"
//         });
//         if(!response.ok){
//             const data = await response.json();
//             // console.log(data);
//             // console.log(data.error);
//
//             showErrorPage(data.error);
//
//         }
//         const data = await response.json();
//
//         return data.data;
//
//     }catch (err){
//         console.log(err);
//         showErrorPage("현재 채팅방 참여자가 아닙니다.");
//     }
//
//
//
// }

async function checkIsRoomParticipant() {
    try{
        const response = await fetch(`/chat/check/my/rooms?roomId=${roomId}`,{
            method: "GET",
            credentials: "include"
        })

        if(!response.ok){
            const data = await response.json();
            showErrorPage(data.error);
        }
    }catch(err){
        showErrorPage("현재 채팅방 참가자가 아닙니다.");
    }

}

async function getChatHistory() {
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

    }catch (err){
        console.error("가져올 이전 기록이 없습니다.");
        return;
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

function setSidePanel(){
    const openBtn = document.querySelector("#openPanelBtn");
    const closeBtn = document.querySelector("#closePanelBtn");
    const panel = document.querySelector("#sidePanel");
    //const overlay = document.querySelector("#overlay");

    openBtn.addEventListener("click", () => {
        panel.classList.add("show");
        //overlay.classList.add("show");
    });

    closeBtn.addEventListener("click", () => {
        panel.classList.remove("show");
        //overlay.classList.remove("show");
    });

    // overlay.addEventListener("click", () => {
    //     panel.classList.remove("show");
    //     overlay.classList.remove("show");
    // });
}

async function initParticipantList(){

    const items = await getParticipantList(roomId);
    const content = document.querySelector(".side-panel-content");
    content.replaceChildren();


    const myCard = document.createElement("div");
    const myName = document.createElement("span");

    myName.textContent = "1. 나";
    myCard.appendChild(myName);
    content.appendChild(myCard);

    items.forEach((item, index)=>{
        if(item.memberEmail===loginEmail){
            return;
        }
        const card = document.createElement("div");
        card.innerHTML=`
                      <div class="member-name">${index+2}. ${item.memberName}</div>
                      <div class="button-group">
                          <button class="enter-btn btn">입장</button>
                          <button class="exit-btn btn"></button>
                      </div>                      
                                                
                        `;

        setButton(card,item);
        content.appendChild(card);

    })
}

async function getParticipantList(){

    try{
        const response = await fetch(`/chat/group/participants?roomId=${roomId}`,{
            method: "GET",
            credentials: "include"
        });
        if(!response.ok)
            throw new Error(`HTTP error! Status:${response.status}`);
        const data = await response.json();

        console.log(data);
        return data.data;


    }catch(err){
        console.error(err);
    }
}

function setButton(card,item){
    const enterBtn = card.querySelector(".enter-btn");
    const exitBtn = card.querySelector(".exit-btn");

    //나와 연결된 1대1 채팅방이 있는 경우 and 내가 상대방을 차단한 경우
    if(item.isMyPrivateChatPartner===true && item.isBlock===true) {
        enterBtn.disabled = true;
        exitBtn.textContent = "해제"

        // 차단해제하는 경우
        exitBtn.addEventListener("click",async (e)=>{
            // let reply = confirm("정말 차단 해제 하시겠습니까?");
            //
            // if(reply){
            //     try{
            //         const response = await fetch(`/chat/room/private/api/unblock?roomId=${item.privateRoomId}`,{
            //             method: "GET",
            //             credentials: "include"
            //         });
            //         if(!response.ok)
            //             throw new Error(`HTTP error! Status:${response.status}`);
            //         initParticipantList();
            //
            //     }catch (err){
            //         console.log(err);
            //     }
            //
            // }else{
            //     e.preventDefault();
            // }

            Swal.fire({
                text: '정말 차단 해제 하시겠습니까?',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonText: '예',
                cancelButtonText: '아니요'
            }).then(async (result) => {
                if (result.isConfirmed) {
                    // 사용자가 '네'를 눌렀을 때 처리
                    try{
                        const response = await fetch(`/chat/room/private/api/unblock?roomId=${item.privateRoomId}`,{
                            method: "GET",
                            credentials: "include"
                        });
                        if(!response.ok)
                            throw new Error(`HTTP error! Status:${response.status}`);
                        initParticipantList();

                    }catch (err){
                        console.log(err);
                    }
                } else {
                    // 사용자가 '아니요' 눌렀을 때
                    e.preventDefault();
                }
            });
        });


    }else if(item.isMyPrivateChatPartner===true && item.isBlock!==true) {
        //나와 연결된 1대1 채팅방이 있는 경우 and 내가 상대방을 차단하지 않은 경우
        exitBtn.textContent = "차단";

        enterBtn.addEventListener("click",()=>{
            window.open(`/chat/my/room?roomId=${item.privateRoomId}`,"_blank","noopener,noreferrer");
        });

        // 차단하는 경우
        exitBtn.addEventListener("click",async (e)=>{
            // let reply = confirm("정말 차단 하시겠습니까?");
            //
            // if(reply){
            //     try{
            //         const response = await fetch(`/chat/room/private/api/block?roomId=${item.privateRoomId}`,{
            //             method: "GET",
            //             credentials: "include"
            //         });
            //         if(!response.ok)
            //             throw new Error(`HTTP error! Status:${response.status}`);
            //         initParticipantList();
            //
            //     }catch (err){
            //         console.log(err);
            //     }
            //
            // }else{
            //     e.preventDefault();
            // }

            Swal.fire({
                text: '정말 차단 하시겠습니까?',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonText: '예',
                cancelButtonText: '아니요'
            }).then(async (result) => {
                if (result.isConfirmed) {
                    // 사용자가 '네'를 눌렀을 때 처리
                    try{
                        const response = await fetch(`/chat/room/private/api/block?roomId=${item.privateRoomId}`,{
                            method: "GET",
                            credentials: "include"
                        });
                        if(!response.ok)
                            throw new Error(`HTTP error! Status:${response.status}`);
                        initParticipantList();

                    }catch (err){
                        console.log(err);
                    }
                } else {
                    // 사용자가 '아니요' 눌렀을 때
                    e.preventDefault();
                }
            });
        });

    }else {
        exitBtn.textContent = "차단";
        exitBtn.disabled = true;

        enterBtn.addEventListener("click",()=>{
            window.open(`/chat/private?receiverId=${item.memberId}`,"_blank","noopener,noreferrer");

            setTimeout(()=>{
                initParticipantList();
            },1000)

        })
    }

}




























