let boardId = '';
const Status = {
    PENDING: "PENDING",
    APPROVED: "APPROVED",
    DENIED: "DENIED",
    CANCELREQUESTED: "CANCELREQUESTED"
}


document.addEventListener("DOMContentLoaded",async ()=>{


    const detailDto = document.querySelector("#matchup-request-detail-dto");
    boardId = Number(detailDto.dataset.boardId);

    const matchDatetime = detailDto.dataset.matchDatetime;
    const matchEndtime = detailDto.dataset.matchEndtime;
    const currentParticipantCount = detailDto.dataset.currentParticipantCount;
    const maxParticipants = detailDto.dataset.maxParticipants;

    calTime(matchDatetime, matchEndtime);
    checkMatchStatus(matchDatetime, matchEndtime);


    loadItems(1) // 프론트는 페이지 번호 시작을 1부터, 헷갈림
})


async function loadItems(page){
    let items = [];
    let pageInfo = {
        page: 0,
        totalPages: 1
    };
    try{
        const response = await fetch(`/matchup/request/board/list?page=${page-1}&board-id=${boardId}`,{

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
    }catch(err){
        console.error(err);
    }


    renderList(items);
    renderPagination(pageInfo);


}
function renderList(items){
    const requestArea = document.querySelector("#request-container");
    requestArea.innerHTML = '';

    if(items.length ===0){
        requestArea.innerHTML = `
            <tr>
                <td colspan="6" class="no-result"> 현재 참가 요청이 없습니다.</td>
            </tr>           
        `;
        return;
    }

    items.forEach(item=>{
        const card = document.createElement("tr");
        card.className = "matchup-card";
        card.innerHTML = `
                        <td class="truncate">${item.applicantName}</td>
                        <td class="truncate">${item.selfIntro}</td>
                        <td>${item.participantCount}</td>
                        <td>${manageRequestInfo(item)}</td>
                        <td>
                            요청: ${item.matchupRequestSubmittedCount} <br/>
                            취소: ${item.matchupCancelSubmittedCount}
                        </td>
                        <td class="button-group">
                            <button id="approvedBtn">참가 요청 승인</button>
                            <button id="deniedBtn">참가 요청 반려</button><br/>
                            <hr style="width: 90%; margin-left: 0;">
                            <button id="approveWithdrawRequestBtn">취소 요청 승인</button>
                            <button id="denyWithdrawRequestBtn">취소 요청 반려</button>
                        </td>
                        `;
        setDecision(card,item);
        markIfPastMatchdatetime(card, item);
        requestArea.appendChild(card);

    })
}

function renderPagination(pageInfo){

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
            loadItems(1);

        });
        pagingArea.appendChild(firstBtn);
    }

    // 이전 블록으로 이동
    if (startPage > 1){
        const prevBtn = document.createElement("button");
        prevBtn.textContent = "<";
        prevBtn.addEventListener("click",()=>{
            loadItems(startPage-1);

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
            loadItems(i);
        })
        pagingArea.appendChild(btn);
    }

    // 다음 블록으로 이동
    if(endPage < pageInfo.totalPages){
        const nextBtn = document.createElement("button");
        nextBtn.textContent = ">";
        nextBtn.addEventListener("click",()=>{
            loadItems(endPage+1);

        })
        pagingArea.appendChild(nextBtn);
    }

    // 마지막 블록으로 이동

    if(endPage<pageInfo.totalPages){
        const lastBtn = document.createElement("button");
        lastBtn.textContent  = ">>";
        lastBtn.addEventListener("click",()=>{
            loadItems(pageInfo.totalPages);
        })
        pagingArea.appendChild(lastBtn);


    }

}

function calTime(matchDatetime, matchEndtime){
    //console.log(matchDatetime);
    //console.log(matchDuration);

    const date = new Date(matchDatetime);
    const end = new Date(matchEndtime);
    //console.log(date);
    const matchDateEle = document.querySelector("#match-date");

    const month = date.getMonth()+1;
    const day = date.getDate();

    const startHour = date.getHours();
    const startMinutes = date.getMinutes();

    const endHour = end.getHours();
    const endMinutes = end.getMinutes();


    // const [hour, minute, second] = matchDuration.split(":");
    // const hourNum = parseInt(hour, 10);
    // const minuteNum = parseInt(minute,10);
    //
    // let extraHour = 0
    // let endMinute = 0;
    //
    // if(date.getMinutes()+minuteNum>=60){
    //     extraHour = 1;
    //     endMinute = (date.getMinutes()+minuteNum)%60;
    // }else{
    //     endMinute = date.getMinutes()+minuteNum;
    // }
    //
    // if(startHour+hourNum+extraHour>=24)
    //     endHour = (startHour+hourNum+extraHour) %24;
    // else
    //     endHour = startHour+hourNum+extraHour;

    matchDateEle.textContent = `${month}/${day} ${startHour}시 ${startMinutes}분 - ${endHour}시 ${endMinutes}분`

}

function checkMatchStatus(matchDatetime, matchEndtime){

    const matchStatusEle = document.querySelector("#match-status");

    const matchDate = new Date(matchDatetime);
    const endMatchDate = new Date(matchEndtime);
    const now = new Date();
    // const durationParts = matchDuration.split(":");
    // const matchEnd = new Date(matchDate.getTime() + (parseInt(durationParts[0])*60+parseInt(durationParts[1])) * 60 * 1000);

    if(matchDate <=now && now <= endMatchDate)
        matchStatusEle.innerHTML = "경기 진행";
    else if(endMatchDate<now)
        matchStatusEle.textContent = "경기 종료";
    else
        matchStatusEle.textContent =  "경기 시작전";
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
        (item.matchupStatus===Status.PENDING && item.matchupRequestSubmittedCount===2 && item.matchupCancelSubmittedCount===0 && item.isDeleted===true) ||
        (item.matchupStatus===Status.DENIED && item.matchupRequestSubmittedCount===1 && item.matchupCancelSubmittedCount===0 && item.isDeleted===true)
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

function setDecision(card,item){
    //matchupStatus, matchupRequestSubmittedCount, matchupCancelSubmittedCount, isDeleted, matchDatetime, boardId, requestId, currentParticipantCount, maxParticipants, participantCount


    const approvedBtn = card.querySelector("#approvedBtn");
    const deniedBtn = card.querySelector("#deniedBtn");
    const approveWithdrawRequestBtn = card.querySelector("#approveWithdrawRequestBtn");
    const denyWithdrawRequestBtn = card.querySelector("#denyWithdrawRequestBtn");

    // 클릭 시 체크 날짜, 인원수 --> DB에서도 체크해야됨
    const matchDate = new Date(item.matchDatetime);
    let now = new Date();

    approvedBtn.addEventListener("click",(e)=>{
        now = new Date();
        if(matchDate<now){
            e.preventDefault();
            e.target.classList.add("disabled");
            //alert("경기 시작 시간이 지나 승인을 할 수 없습니다.");
            Swal.fire({text: '경기 시작 시간이 지나 승인을 할 수 없습니다.', icon: 'warning', confirmButtonText: '확인'});
        }else{
            window.location.href = `/matchup/request/approve?board-id=${item.boardId}&request-id=${item.requestId}`;
        }
    });

    deniedBtn.addEventListener("click",(e)=>{
        now = new Date();
        if(matchDate<now){
            e.preventDefault();
            e.target.classList.add("disabled");
            //alert("경기 시작 시간이 지나 반려를 할 수 없습니다.");
            Swal.fire({text: '경기 시작 시간이 지나 반려를 할 수 없습니다.', icon: 'warning', confirmButtonText: '확인'});
        }else{
            window.location.href = `/matchup/request/deny?board-id=${item.boardId}&request-id=${item.requestId}`;
        }
    })

    approveWithdrawRequestBtn.addEventListener("click",(e)=>{
        now = new Date();
        if(matchDate<now){
            e.preventDefault();
            e.target.classList.add("disabled");
            //alert("경기 시작 시간이 지나 취소 요청 승인을 할 수 없습니다.");
            Swal.fire({text: '경기 시작 시간이 지나 취소 요청 승인을 할 수 없습니다.', icon: 'warning', confirmButtonText: '확인'});
        }else{
            window.location.href = `/matchup/request/approve?board-id=${item.boardId}&request-id=${item.requestId}`;
        }
    })

    denyWithdrawRequestBtn.addEventListener("click",(e)=>{
        now = new Date();
        if(matchDate<now){
            e.preventDefault();
            e.target.classList.add("disabled");
            //alert("경기 시작 시간이 지나 취소 요청 반려를 할 수 없습니다.");
            Swal.fire({text: '경기 시작 시간이 지나 취소 요청 반려를 할 수 없습니다.', icon: 'warning', confirmButtonText: '확인'});
        }else{
            window.location.href = `/matchup/request/deny?board-id=${item.boardId}&request-id=${item.requestId}`;
        }
    })

    // 참가 요청에 대한 승인/반려
    if(matchDate<=now){
        // 참가 승인 불가능
        approvedBtn.classList.add("disabled");

        // 참가 반려 불가능
        deniedBtn.classList.add("disabled");

        //취소 승인 불가능
        approveWithdrawRequestBtn.classList.add("disabled");
        //approveWithdrawRequestBtn.href = "#";

        //취소 반려 불가능

        denyWithdrawRequestBtn.classList.add("disabled");
        //denyWithdrawRequestBtn.href = "#";
    }
    else if(
        (item.matchupStatus===Status.PENDING && item.matchupRequestSubmittedCount===1 && item.matchupCancelSubmittedCount ===0 && item.isDeleted===false)||
        (item.matchupStatus===Status.PENDING && item.matchupRequestSubmittedCount===2 && item.matchupCancelSubmittedCount ===0 && item.isDeleted===false)
    ){
        // approvedBtn.href = `/matchup/request/approve?request-id=${requestId}&board-id=${boardId}`;
        // deniedBtn.href = `/matchup/request/deny?request-id=${requestId}&board-id=${boardId}`;

        // 참가 승인 가능

        // 참가 반려 가능

        //취소 승인 불가능
        approveWithdrawRequestBtn.classList.add("disabled");
        //approveWithdrawRequestBtn.href = "#";

        //취소 반려 불가능

        denyWithdrawRequestBtn.classList.add("disabled");
        //denyWithdrawRequestBtn.href = "#";

    }
    //승인 취소 요청에 대한 승인/반려
    else if(
        (item.matchupStatus===Status.CANCELREQUESTED && item.matchupRequestSubmittedCount===1 && item.matchupCancelSubmittedCount ===1 && item.isDeleted===false) ||
        (item.matchupStatus===Status.CANCELREQUESTED && item.matchupRequestSubmittedCount===2 && item.matchupCancelSubmittedCount ===1 && item.isDeleted===false)
    ){
        // approvedBtn.href = `/matchup/request/approve?request-id=${requestId}&board-id=${boardId}`;
        // deniedBtn.href = `/matchup/request/deny?request-id=${requestId}&board-id=${boardId}`;

        // 참가 승인 불가능
        approvedBtn.classList.add("disabled");
        //approvedBtn.href = "#";

        // 참가 반려 불가능
        deniedBtn.classList.add("disabled");
        //deniedBtn.href = "#";

        //취소 승인 가능

        //취소 반려 가능

    }else{
        // 예측하지 못한 오류 발생한 경우

        // 참가 승인 불가능
        approvedBtn.classList.add("disabled");
        //approvedBtn.href = "#";

        // 참가 반려 불가능
        deniedBtn.classList.add("disabled");
        //deniedBtn.href = "#";

        //취소 승인 불가능
        approveWithdrawRequestBtn.classList.add("disabled");
        //approveWithdrawRequestBtn.href = "#";

        //취소 반려 불가능
        denyWithdrawRequestBtn.classList.add("disabled");
        //denyWithdrawRequestBtn.href = "#";
    }

    // 마지막에 인원수 체크, 승인 버튼 없애기
    if(item.currentParticipantCount+item.participantCount>item.maxParticipants){

        // 참가 승인 불가능
        approvedBtn.classList.add("disabled");
        //approvedBtn.href = "#";

        // 참가 반려 불가능

        // 취소 승인 가능

        // 취소 반려 가능

    }
}

/*경기 시작 시간이 지났다면 회색으로 표현*/
function markIfPastMatchdatetime(card, item){
    const matchDate = new Date(item.matchDatetime);
    const now = new Date();
    if(matchDate<now){
        const tds = card.querySelectorAll("td");
        tds.forEach(td =>{
            td.style.backgroundColor = "lightgray";
        })
    }

}

function goBack(){
    if (document.referrer && document.referrer !== location.href) {
        window.history.back();
    } else {
        window.location.href = "/matchup/board";
    }
}

