

document.addEventListener("DOMContentLoaded",async ()=>{
    getMyChatRooms();

    renderChatList();
})


async function renderChatList() {
    const chatRoomList = document.querySelector(".chat-room-list");

    const items = await getMyChatRooms();

    if(items.data.length ===0){
        chatRoomList.innerHTML = `
            <div class="no-result">
                현재 참여 중인 채팅방이 없습니다.
            </div>
        `;
        return;
    }

    items.data.forEach(item =>{
        const card = document.createElement("div");
        card.className = "chat-card";

        card.innerHTML = `
            <div class="chat-col chat-name"><strong>${setChatName(item.roomName)}</strong></div>
            <div class="chat-col chat-group"><strong>${expressIsGroutChat(item.isGroupChat)}</strong></div>
            <div class="chat-col chat-unread"><strong>${item.unReadCount}</strong></div>
            <div class="chat-col chat-actions">
                <button class="btn enter-btn"></button>
                <button class="btn exit-btn"></button>
            </div>           
        `;

        setButton(card,item);
        chatRoomList.appendChild(card);

    });

}


async function getMyChatRooms(){
    try{
        const response = await fetch("/chat/my/rooms",{
            method: "POST",
            credentials: "include"
        });

        if(!response.ok)
            throw new Error(`HTTP error! Status:${response.status}`)



        const items = await response.json();
        //console.log(items);
        return items;

    }catch (err){
        consolse.log(err);
    }

    return [];

}

function setButton(card,item){
    const enterBtn = card.querySelector(".enter-btn");
    enterBtn.textContent = "입장";

    const exitBtn = card.querySelector(".exit-btn");
    if(item.isGroupChat === true){

        enterBtn.addEventListener("click",()=>{
            window.open(`/chat/group/room?roomId=${item.roomId}`,"_blank","noopener,noreferrer");
        });

        exitBtn.textContent = "퇴장";
        exitBtn.disabled = true;
    }

    else{

        if(item.isBlock===true){
            exitBtn.textContent = "해제";
            exitBtn.addEventListener("click",(e)=>{
                //let reply = confirm("정말 차단 해제 하시겠습니까?");

                // if(reply){
                //     window.location.href = `/chat/room/private/unblock?roomId=${item.roomId}`;
                // }else{
                //     e.preventDefault();
                // }

                Swal.fire({
                    text: '정말 차단 해제 하시겠습니까?',
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonText: '예',
                    cancelButtonText: '아니요'
                }).then((result) => {
                    if (result.isConfirmed) {
                        // 사용자가 '네'를 눌렀을 때 처리
                        window.location.href = `/chat/room/private/unblock?roomId=${item.roomId}`;
                    } else {
                        // 사용자가 '아니요' 눌렀을 때
                        e.preventDefault();
                    }
                });

            });

            enterBtn.disabled=true;

        }else{
            exitBtn.textContent = "차단";
            exitBtn.addEventListener("click",(e)=>{
                // let reply = confirm("정말 차단 하시겠습니까?");
                // if(reply){
                //     window.location.href = `/chat/room/private/block?roomId=${item.roomId}`;
                // }else{
                //     e.preventDefault();
                // }

                Swal.fire({
                    text: '정말 차단 하시겠습니까?',
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonText: '예',
                    cancelButtonText: '아니요'
                }).then((result) => {
                    if (result.isConfirmed) {
                        // 사용자가 '네'를 눌렀을 때 처리
                        window.location.href = `/chat/room/private/block?roomId=${item.roomId}`;
                    } else {
                        // 사용자가 '아니요' 눌렀을 때
                        e.preventDefault();
                    }
                });
            });

            enterBtn.addEventListener("click",()=>{
                window.open(`/chat/my/room?roomId=${item.roomId}`,"_blank","noopener,noreferrer");
            });
        }
    }
}


function expressIsGroutChat(isGroupChat){
    if(isGroupChat === true)
        return "그룹 채팅";
    else
        return "1대1 채팅";

}

function setChatName(roomName){

    return roomName.slice(0,-10);


}
