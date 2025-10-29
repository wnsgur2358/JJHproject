document.addEventListener("DOMContentLoaded", async () => {
    getMyTeamChatRooms();
});

async function getMyTeamChatRooms() {
    try {
        const response = await fetch("/chat/my/rooms/team", {
            method: "GET",
            credentials: "include"
        });

        if (!response.ok) {
            const error = await response.json();
            console.error("❌ 팀 채팅방 로딩 실패:", error);
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const result = await response.json();
        const rooms = result.data;

        const chatRoomList = document.querySelector(".chat-room-list");

        // ✅ Step 1: Clear existing content to avoid duplicates
        chatRoomList.innerHTML = "";

        if (!rooms || rooms.length === 0) {
            chatRoomList.innerHTML = `
                <div class="no-result">
                    현재 참여 중인 팀 채팅방이 없습니다.
                </div>
            `;
            return;
        }

        // ✅ ADD THIS HERE
        function expressIsGroupChat(isGroupChat) {
            return isGroupChat === true ? "그룹 채팅" : "1:1 채팅";
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
                        // let reply = confirm("정말 차단 해제 하시겠습니까?");
                        // if(reply){
                        //     window.location.href = `/chat/room/private/team/unblock?roomId=${item.roomId}`;
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
                                window.location.href = `/chat/room/private/team/unblock?roomId=${item.roomId}`;
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
                        //     window.location.href = `/chat/room/private/team/block?roomId=${item.roomId}`;
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
                                window.location.href = `/chat/room/private/team/block?roomId=${item.roomId}`;
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



        // ✅ Sort rooms to always show group chat on top
        rooms
            .forEach(room => {
                const card = document.createElement("div");
                card.className = "chat-card";

            card.innerHTML = `
                <div class="chat-col chat-name"><strong>${room.roomName}</strong></div>
                <div class="chat-col chat-group"><strong>${expressIsGroupChat(room.isGroupChat)}</strong></div>
                <div class="chat-col chat-unread"><strong>${room.unReadCount || 0}</strong></div>
                <div class="chat-col chat-actions">
                    <button class="btn enter-btn">입장</button>
                     <button class="btn exit-btn"></button> <!-- ✅ Add this line -->
                </div>
            `;


                setButton(card, room); // ✅ Add this line
            chatRoomList.appendChild(card);
        });

    } catch (err) {
        console.error("🚨 오류 발생:", err);
        document.querySelector(".chat-room-list").innerHTML = `
            <div class="error-message">
                팀 채팅방을 불러오는 중 문제가 발생했습니다.
            </div>
        `;
    }
}