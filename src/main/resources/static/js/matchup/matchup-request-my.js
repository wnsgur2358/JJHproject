let sportsType = '';
let dateFilter = '';
let availableFilter = false;
let lastFilterValues = {};
const Status = {
    PENDING: "PENDING",
    APPROVED: "APPROVED",
    DENIED: "DENIED",
    CANCELREQUESTED: "CANCELREQUESTED"
}
document.addEventListener("DOMContentLoaded",async ()=>{

    lastFilterValues={
        "sportsType": sportsType,
        "dateFilter": dateFilter,
        "availableFilter": availableFilter
    }

    document.querySelector("#sports-type").addEventListener("change",(e)=>{
        sportsType = e.target.value;
    })

    document.querySelector("#date-filter").addEventListener("change",(e)=>{
        dateFilter = e.target.value;
        //console.log(dateFilter);
    })

    document.querySelector("#availableOnly").addEventListener("change",(e)=>{
        availableFilter = e.target.checked;
        // 체크O: true
        // 체크x: false
    })

    document.querySelector("#filterBtn").addEventListener("click",(e)=>{
        const isSame = lastFilterValues.sportsType === sportsType &&
            lastFilterValues.dateFilter === dateFilter &&
            lastFilterValues.availableFilter === availableFilter;

        if(isSame){
            e.preventDefault();
            console.log("검색 조건이 변하지 않았습니다.");
        }else{
            lastFilterValues.sportsType = sportsType;
            lastFilterValues.dateFilter = dateFilter;
            lastFilterValues.availableFilter = availableFilter;
            loadItems(1, sportsType, dateFilter, availableFilter);
        }
    })
    loadItems(1) // 프론트는 페이지 번호 시작을 1부터, 헷갈림
})


async function loadItems(page, sportsType='', dateFilter='', availableFilter=false){
    let items = [];
    let pageInfo = {
        page: 0,
        totalPages: 1
    };
    try{
        const response = await fetch(`/matchup/request/my/list?page=${page-1}&sportsType=${sportsType}&date=${dateFilter}&availableFilter=${availableFilter}`,{

            method: "GET",
            credentials: "include"
        });
        if(!response.ok)
            throw new Error(`HTTP error! Status:${response.status}`)
        const data = await response.json();
        //console.log(data);
        items = data.data.items;
        pageInfo = data.data.pageInfo;
        //console.log(pageInfo);
    }catch (err){
        console.error(err);
    }

    renderList(items);
    renderPagination(pageInfo,sportsType, dateFilter, availableFilter);

}
function renderList(items){
    const boardArea = document.querySelector("#request-container");
    boardArea.innerHTML = '';

    if(items.length ===0){
        boardArea.innerHTML = `
            <tr>
                <td colspan="10" class="no-result"> 현재 참가 요청 내역이 없습니다.</td>
            </tr>           
        `;
        return;
    }

    items.forEach(item=>{
        const date = new Date(item.matchDatetime);
        const end = new Date(item.matchEndtime);

        const card = document.createElement("tr");
        card.className = "matchup-card";
        card.innerHTML = `                                             
                <td>${setSportsType(item.sportsTypeName)}</td>
                <td class="truncate">${item.sportsFacilityAddress}</td>
                <td>📅 ${date.getMonth() + 1}/${date.getDate()} ${date.getHours()}시 ${date.getMinutes()}분 - 
                                ${end.getHours()}시 ${end.getMinutes()}분</td>
                <td>${checkMatchStatus(item)}</td>
                <td>( ${item.currentParticipantCount} / ${item.maxParticipants} )</td>
                <td>${item.participantCount}</td>
                <td>${manageRequestInfo(item)}</td>
                <td>
                    요청: ${item.matchupRequestSubmittedCount} <br/>
                    취소: ${item.matchupCancelSubmittedCount}
                </td>                
                <td>
                    <button onclick="location.href='/matchup/board/detail?matchup-board-id=${item.boardId}'" class="board-detail button-group"> 게시글</button><br/>
                    <button onclick="location.href='/matchup/request/detail?request-id=${item.requestId}'" class="request-detail button-group">참가요청</button>
                </td>               
                <td><button class="group-chat disabled button-group">단체 채팅</button></td>
                `;

        markIfPastMatchdatetime(card, item);
        setGroupChatButton(card, item);
        boardArea.appendChild(card);


    })
}

function renderPagination(pageInfo, sportsType, dateFilter, availableFilter){

    // 프론트는 페이지 시작번호 1부터로 헷갈림
    const pageBlockSize = 5;
    // 프론트 측 page 시작 번호 1부터 변경
    const curPage = pageInfo.page + 1;


    const pagingArea = document.querySelector("#paging-container");
    pagingArea.innerHTML = '';


    const currentBlock = Math.floor((curPage-1)/pageBlockSize);
    const startPage = currentBlock * pageBlockSize+1;
    const endPage = Math.min(startPage + pageBlockSize-1,pageInfo.totalPages);

    // 첫 번째 블록으로 이동
    if (startPage > 1){
        const firstBtn = document.createElement("button");
        firstBtn.textContent = "<<";
        firstBtn.addEventListener("click",()=>{
            loadItems(1, sportsType, dateFilter, availableFilter);

        });
        pagingArea.appendChild(firstBtn);
    }

    // 이전 블록으로 이동
    if (startPage > 1){
        const prevBtn = document.createElement("button");
        prevBtn.textContent = "<";
        prevBtn.addEventListener("click",()=>{
            loadItems(startPage-1, sportsType, dateFilter, availableFilter);

        });
        pagingArea.appendChild(prevBtn);
    }

    // 현재 블록의 페이지 번호 버튼들
    for(let i=startPage; i<=endPage;i++){
        const btn = document.createElement("button");
        btn.textContent = i;
        if( i=== curPage)
            //btn.disabled = true;
            btn.classList.add("active");

        btn.addEventListener("click",()=>{
            loadItems(i,sportsType, dateFilter, availableFilter);
        })
        pagingArea.appendChild(btn);
    }

    // 다음 블록으로 이동
    if(endPage < pageInfo.totalPages){
        const nextBtn = document.createElement("button");
        nextBtn.textContent = ">";
        nextBtn.addEventListener("click",()=>{
            loadItems(endPage+1, sportsType, dateFilter, availableFilter);

        })
        pagingArea.appendChild(nextBtn);
    }

    // 마지막 블록으로 이동

    if(endPage<pageInfo.totalPages){
        const lastBtn = document.createElement("button");
        lastBtn.textContent  = ">>";
        lastBtn.addEventListener("click",()=>{
            loadItems(pageInfo.totalPages, sportsType, dateFilter, availableFilter);
        })
        pagingArea.appendChild(lastBtn);


    }

}

function calTime(item, startHour, startMinute){


    const [hour, minute, second] = item.matchDuration.split(":");
    const hourNum = parseInt(hour, 10);
    const minuteNum = parseInt(minute,10);
    let extraHour = 0
    let endMinute = 0;
    if(startMinute+minuteNum>=60){
        extraHour = 1;
        endMinute = (startMinute+minuteNum)%60;
    }else{
        endMinute = startMinute+minuteNum;
    }

    if(startHour+hourNum+extraHour>=24)
        endHour = (startHour+hourNum+extraHour) %24;
    else
        endHour = startHour+hourNum+extraHour;

    return `${endHour}시 ${endMinute}분`

}

function checkMatchStatus(item){

    const matchDate = new Date(item.matchDatetime);
    const endMatchDate = new Date(item.matchEndtime);
    const now = new Date();
    // const durationParts = item.matchDuration.split(":");
    // const matchEnd = new Date(matchDate.getTime() + (parseInt(durationParts[0])*60+parseInt(durationParts[1])) * 60 * 1000);

    if(matchDate <now && now <= endMatchDate)
        return "경기 진행";
    else if(endMatchDate<now)
        return "경기 종료";
    else
        return "경기 시작전"
}

function manageRequestInfo(item){
    const matchDate = new Date(item.matchDatetime);
    const now = new Date();

    // console.log(item.matchupStatus);
    // console.log(item.matchupRequestSubmittedCount);
    // console.log(item.matchupCancelSubmittedCount);
    // console.log(item.isDeleted);
    // if(item.matchupStatus === Status.PENDING)
    //     console.log("enum 사용");

    // 1. 참가 요청 후 승인 대기
    if(
        (item.matchupStatus ===Status.PENDING && item.matchupRequestSubmittedCount===1 && item.matchupCancelSubmittedCount===0 && item.isDeleted===false) ||
        (item.matchupStatus===Status.PENDING && item.matchupRequestSubmittedCount===2 && item.matchupCancelSubmittedCount===0 && item.isDeleted ===false)
    ){
        return "승인 대기";
    }
    // 2. 참가 요청 삭제
    else if(
        (item.matchupStatus===Status.PENDING && item.matchupRequestSubmittedCount===1 && item.matchupCancelSubmittedCount===0 && item.isDeleted===true) ||
        (item.matchupStatus===Status.PENDING && item.matchupRequestSubmittedCount===2 && item.matchupCancelSubmittedCount===0 && item.isDeleted===true)

    ){
        return "요청 삭제됨";
    }
    // 3. 참가 요청 승인
    else if(
        (item.matchupStatus===Status.APPROVED && item.matchupRequestSubmittedCount===2 && item.matchupCancelSubmittedCount===0 && item.isDeleted===false)||
        (item.matchupStatus===Status.APPROVED && item.matchupRequestSubmittedCount===1 && item.matchupCancelSubmittedCount===0 && item.isDeleted===false)
    ){
        return "승인됨";
    }
    // 4. 참가 요청 반려
    else if(
        (item.matchupStatus === Status.DENIED && item.matchupRequestSubmittedCount ===2 && item.matchupCancelSubmittedCount ===0 && item.isDeleted ===false) ||
        (item.matchupStatus === Status.DENIED && item.matchupRequestSubmittedCount ===1 && item.matchupCancelSubmittedCount ===0 && item.isDeleted ===false)
    ){
        return "반려됨";
    }
    // 8. 승인 취소 요청을 했으나 경기 시간이 지나 자동 참가 처리
    else if(
        (matchDate <= now) &&
        (
            (item.matchupStatus === Status.CANCELREQUESTED && item.matchupRequestSubmittedCount ===2 && item.matchupCancelSubmittedCount ===1 && item.isDeleted===false) ||
            (item.matchupStatus === Status.CANCELREQUESTED && item.matchupRequestSubmittedCount ===1 && item.matchupCancelSubmittedCount ===1 && item.isDeleted===false)
        )
    ){
        return "자동 참가"
    }
    // 5. 승인 취소 요청 상태
    else if(
        (item.matchupStatus === Status.CANCELREQUESTED && item.matchupRequestSubmittedCount ===2 && item.matchupCancelSubmittedCount ===1 && item.isDeleted===false) ||
        (item.matchupStatus === Status.CANCELREQUESTED && item.matchupRequestSubmittedCount ===1 && item.matchupCancelSubmittedCount ===1 && item.isDeleted===false)
    ){
        return "승인 취소 요청";
    }
    // 6. 승인 취소 요청이 승인
    else if(
        (item.matchupStatus===Status.CANCELREQUESTED && item.matchupRequestSubmittedCount === 2 && item.matchupCancelSubmittedCount===1 && item.isDeleted===true) ||
        (item.matchupStatus===Status.CANCELREQUESTED && item.matchupRequestSubmittedCount === 1 && item.matchupCancelSubmittedCount===1 && item.isDeleted===true)
    ){
        return "취소 요청 승인";
    }
    // 7. 승인 취소 요청이 반려
    else if(
        (item.matchupStatus===Status.APPROVED && item.matchupRequestSubmittedCount===2 && item.matchupCancelSubmittedCount===1 && item.isDeleted ===false) ||
        (item.matchupStatus===Status.APPROVED && item.matchupRequestSubmittedCount===1 && item.matchupCancelSubmittedCount===1 && item.isDeleted ===false)
    ){
        return "취소 요청 반려";
    }else{
        return "서버 오류";
    }
}

function setGroupChatButton(card, item){
    const matchDate = new Date(item.matchDatetime);
    const now = new Date();
    const threeDaysLater = new Date(matchDate.getTime()+3*24*60*60*1000);


    const groupChatBtn = card.querySelector(".group-chat");
    if(threeDaysLater<now){
        groupChatBtn.classList.add("disabled");
    }
    else if(
        // 1. 참가 요청 후 승인 대기
        (item.matchupStatus ===Status.PENDING && item.matchupRequestSubmittedCount===1 && item.matchupCancelSubmittedCount===0 && item.isDeleted===false) ||
        (item.matchupStatus===Status.PENDING && item.matchupRequestSubmittedCount===2 && item.matchupCancelSubmittedCount===0 && item.isDeleted ===false) ||

        // 2. 참가 요청 삭제
        (item.matchupStatus===Status.PENDING && item.matchupRequestSubmittedCount===1 && item.matchupCancelSubmittedCount===0 && item.isDeleted===true) ||
        (item.matchupStatus===Status.PENDING && item.matchupRequestSubmittedCount===2 && item.matchupCancelSubmittedCount===0 && item.isDeleted===true) ||

        // 4. 참가 요청 반려
        (item.matchupStatus === Status.DENIED && item.matchupRequestSubmittedCount ===2 && item.matchupCancelSubmittedCount ===0 && item.isDeleted ===false) ||
        (item.matchupStatus === Status.DENIED && item.matchupRequestSubmittedCount ===1 && item.matchupCancelSubmittedCount ===0 && item.isDeleted ===false)||

        // 6. 승인 취소 요청이 승인
        (item.matchupStatus===Status.CANCELREQUESTED && item.matchupRequestSubmittedCount === 2 && item.matchupCancelSubmittedCount===1 && item.isDeleted===true) ||
        (item.matchupStatus===Status.CANCELREQUESTED && item.matchupRequestSubmittedCount === 1 && item.matchupCancelSubmittedCount===1 && item.isDeleted===true)

    ){
        groupChatBtn.classList.add("disabled");

    }else if(
        // 3. 참가 요청 승인
        (item.matchupStatus===Status.APPROVED && item.matchupRequestSubmittedCount===2 && item.matchupCancelSubmittedCount===0 && item.isDeleted===false)||
        (item.matchupStatus===Status.APPROVED && item.matchupRequestSubmittedCount===1 && item.matchupCancelSubmittedCount===0 && item.isDeleted===false) ||

        // 5. 승인 취소 요청 상태
        (item.matchupStatus === Status.CANCELREQUESTED && item.matchupRequestSubmittedCount ===2 && item.matchupCancelSubmittedCount ===1 && item.isDeleted===false) ||
        (item.matchupStatus === Status.CANCELREQUESTED && item.matchupRequestSubmittedCount ===1 && item.matchupCancelSubmittedCount ===1 && item.isDeleted===false) ||

        // 7. 승인 취소 요청이 반려
        (item.matchupStatus===Status.APPROVED && item.matchupRequestSubmittedCount===2 && item.matchupCancelSubmittedCount===1 && item.isDeleted ===false) ||
        (item.matchupStatus===Status.APPROVED && item.matchupRequestSubmittedCount===1 && item.matchupCancelSubmittedCount===1 && item.isDeleted ===false)

        // 8. 승인 취소 요청을 했으나 경기 시간이 지나 자동 참가 처리
        // (
        //     (matchDate <= now) &&
        //     (
        //         (item.matchupStatus === Status.CANCELREQUESTED && item.matchupRequestSubmittedCount ===2 && item.matchupCancelSubmittedCount ===1 && item.isDeleted===false) ||
        //         (item.matchupStatus === Status.CANCELREQUESTED && item.matchupRequestSubmittedCount ===1 && item.matchupCancelSubmittedCount ===1 && item.isDeleted===false)
        //     )
        // )

    ){
        // groupChatBtn.href=`/chat/group/room?roomId=${item.roomId}`;
        // groupChatBtn.href = `/chat/group/room?roomId=${item.roomId}`;
        // groupChatBtn.target = "_blank";
        // groupChatBtn.classList.remove("disabled");

        groupChatBtn.classList.remove("disabled");
        groupChatBtn.addEventListener("click",()=>{
            window.open(`/chat/group/room?roomId=${item.roomId}`,"_blank","noopener,noreferrer");
        })


    }else{
        groupChatBtn.classList.add("disabled");
    }

}

function setSportsType(sportsTypeName){
    if(sportsTypeName ==="SOCCER"){
        return `
                <span style="color: #1abc9c;">SOCCER</span>
                `
    }else{
        return `
                <span style="color: #e67e22;">FUTSAL</span>
                `
    }
}


/*경기 시작 시간이 지났다면 회색으로 표현*/
function markIfPastMatchdatetime(card, item){
    const matchDate = new Date(item.matchDatetime);
    const now = new Date();
    if(matchDate<=now){
        const tds = card.querySelectorAll("td");
        tds.forEach(td =>{
            td.style.backgroundColor = "lightgray";
        })
    }
}

function goBack() {

    if (document.referrer && document.referrer !== location.href) {
        window.history.back();
    } else {
        window.location.href = "/matchup/board"; // fallback URL
    }
}
