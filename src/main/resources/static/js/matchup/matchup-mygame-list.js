document.addEventListener("DOMContentLoaded",()=>{
    loadItems(1) // 프론트는 페이지 번호 시작을 1부터, 헷갈림

});

async function loadItems(page){
    let items = [];
    let pageInfo = {
        page: 0,
        totalPages: 1
    };
    try{
        const response = await fetch(`/matchup/mygame/list?page=${page-1}`,{

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
        console.log(err);
    }

    renderList(items);
    renderPagination(pageInfo);


}
function renderList(items){


    const matchArea = document.querySelector("#match-container");
    matchArea.innerHTML = '';

    if(items.length ===0){
        matchArea.innerHTML = `
            <tr>
                <td colspan="6" class="no-result">경기 참가 이력이 없습니다.</td>
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
                         <td>( ${item.currentParticipantCount} / ${item.maxParticipants} )</td>
                         <td><button onclick="location.href='/matchup/board/detail?matchup-board-id=${item.boardId}'" class="button-group">상세보기</button></td>
                         <td><button class="button-group" id="rating-btn">경기평가</button></td>
                         `;
        setRatingButton(card, item);
        matchArea.appendChild(card);

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

function setRatingButton(card, item){
    const ratingBtn = card.querySelector("#rating-btn");
    if(item.isRatingInitialized){
        ratingBtn.addEventListener("click",()=>{
            window.location.href = `/matchup/rating/page?boardId=${item.boardId}`;
        })

    }else{
        ratingBtn.addEventListener("click",(e)=>{
            e.preventDefault();
            //alert("Matchup 아직 매너 온도 평가 세팅되지 않았습니다. 경기 종료 후 다시 시도해보세요.");
            Swal.fire({text: 'Matchup 아직 매너 온도 평가 세팅되지 않았습니다. 경기 종료 후 다시 시도해보세요.', icon: 'warning', confirmButtonText: '확인'});

        })

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
                `    }
}

function goBack(){
    if (document.referrer && document.referrer !== location.href) {
        window.history.back();
    } else {
        window.location.href = "/matchup/board";
    }
}

