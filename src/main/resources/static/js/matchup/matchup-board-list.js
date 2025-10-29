let myMannerTemperature;
let sportsType = '';
let region = '';
let dateFilter = '';
let availableFilter = false;
let lastFilterValues = {};

document.addEventListener("DOMContentLoaded",async ()=>{
    setButton();

    myMannerTemperature = await getMyMannerTemperature();
    lastFilterValues={
        "sportsType": sportsType,
        "region": region,
        "dateFilter": dateFilter,
        "availableFilter": availableFilter
    }


    document.querySelector("#sports-type").addEventListener("change",(e)=>{
        sportsType = e.target.value;
    })

    document.querySelector("#region").addEventListener("change",(e)=>{
        region = e.target.value;
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
                                        lastFilterValues.region === region &&
                                        lastFilterValues.dateFilter === dateFilter &&
                                        lastFilterValues.availableFilter === availableFilter;
        //console.log(isSame);
        //console.log(availableFilter);
        if(isSame){
            e.preventDefault();
            console.log("검색 조건이 변하지 않았습니다.");
        }else{
            lastFilterValues.sportsType = sportsType;
            lastFilterValues.region = region;
            lastFilterValues.dateFilter = dateFilter;
            lastFilterValues.availableFilter = availableFilter;
            loadItems(1, sportsType, region, dateFilter, availableFilter);
        }
    })
    loadItems(1) // 프론트는 페이지 번호 시작을 1부터, 헷갈림


})

async function loadItems(page, sportsType='', region='', dateFilter='', availableFilter=false){
    let items = [];
    let pageInfo = {
        page: 0,
        totalPages: 1
    };

    try{
        const response = await fetch(`/matchup/board/list?page=${page-1}&sportsType=${sportsType}&region=${region}&date=${dateFilter}&availableFilter=${availableFilter}`,{

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

    } catch (err){
        console.log(err);
    }


    //console.log(items);

    renderList(items);
    renderPagination(pageInfo,sportsType, region, dateFilter, availableFilter);


}
function renderList(items){
    const boardArea = document.querySelector("#board-container");
    boardArea.innerHTML = '';

    if(items.length ===0){
        boardArea.innerHTML = `
            <tr>
                <td colspan="9" class="no-result"> 현재 작성된 게시글이 없습니다.</td>
            </tr>           
        `;
        return;
    }

    items.forEach(item=>{
        const date = new Date(item.matchDatetime);
        const endDate = new Date(item.matchEndtime)

        const card = document.createElement("tr");
        card.innerHTML = `
                        <td>${item.boardId}</td>
                        <td class="truncate-writer">${item.writerName}</td>
                        <td>${setSportsType(item.sportsTypeName)}</td>
                        <td class="truncate">${item.sportsFacilityAddress}</td>
                        <td>📅 ${date.getMonth() + 1}/${date.getDate()} ${date.getHours()}시 ${date.getMinutes()}분 - 
                                ${endDate.getHours()}시 ${endDate.getMinutes()}분</td>
                        <td>${checkStatus(item)}</td>
                        <td>( ${item.currentParticipantCount} / ${item.maxParticipants} )</td>
                        <td> 
                            <div>입장 가능 온도: ${item.minMannerTemperature}</div>
                            <div>현재 내 온도: ${myMannerTemperature}</div>                                      
                        </td>
                        <td><button class="col-detail-btn" onclick="location.href='/matchup/board/detail?matchup-board-id=${item.boardId}'">상세보기</button></td>                   
                        `;
        markIfPastMatchdatetime(card, item);
        boardArea.appendChild(card);

    })
}

function renderPagination(pageInfo, sportsType, region, dateFilter, availableFilter){

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
            loadItems(1, sportsType, region, dateFilter, availableFilter);

        });
        pagingArea.appendChild(firstBtn);
    }

    // 이전 블록으로 이동
    if (startPage > 1){
        const prevBtn = document.createElement("button");
        prevBtn.textContent = "<";
        prevBtn.addEventListener("click",()=>{
            loadItems(startPage-1, sportsType, region, dateFilter, availableFilter);

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
            loadItems(i,sportsType, region, dateFilter, availableFilter);
        })
        pagingArea.appendChild(btn);
    }

    // 다음 블록으로 이동
    if(endPage < pageInfo.totalPages){
        const nextBtn = document.createElement("button");
        nextBtn.textContent = ">";
        nextBtn.addEventListener("click",()=>{
            loadItems(endPage+1, sportsType, region, dateFilter, availableFilter);

        })
        pagingArea.appendChild(nextBtn);
    }

    // 마지막 블록으로 이동

    if(endPage<pageInfo.totalPages){
        const lastBtn = document.createElement("button");
        lastBtn.textContent  = ">>";
        lastBtn.addEventListener("click",()=>{
            loadItems(pageInfo.totalPages, sportsType, region, dateFilter, availableFilter);
        })
        pagingArea.appendChild(lastBtn);


    }

}

// function calTime(item, startHour, startMinute){
//
//
//     const [hour, minute, second] = item.matchDuration.split(":");
//     const hourNum = parseInt(hour, 10);
//     const minuteNum = parseInt(minute,10);
//     let extraHour = 0
//     let endMinute = 0;
//     if(startMinute+minuteNum>=60){
//          extraHour = 1;
//          endMinute = (startMinute+minuteNum)%60;
//     }else{
//         endMinute = startMinute+minuteNum;
//     }
//
//     if(startHour+hourNum+extraHour>=24)
//         endHour = (startHour+hourNum+extraHour) %24;
//     else
//         endHour = startHour+hourNum+extraHour;
//
//     return `${endHour}시 ${endMinute}분`
//
// }

function checkStatus(item){

    const matchDate = new Date(item.matchDatetime);
    const endMatchDate = new Date(item.matchEndtime);
    const now = new Date();
    // const durationParts = item.matchDuration.split(":");
    // const matchEnd = new Date(matchDate.getTime() + (parseInt(durationParts[0])*60+parseInt(durationParts[1])) * 60 * 1000);

    if(matchDate <now && now <= endMatchDate)
        return "경기 진행";
    else if(endMatchDate<now)
        return "경기 종료";
    else if(item.minMannerTemperature > myMannerTemperature)
        return "입장 불가";
    else if(item.currentParticipantCount >= item.maxParticipants)
        return "신청 마감";
    else
        return "신청 가능"
}


async function getMyMannerTemperature(){
    try{
        const response  = await fetch(`/member/search/my-temperature`,{
            method: "GET",
            credentials: "include"
        })
        if(!response.ok)
            throw new Error(`HTTP error! Status:${response.status}`)
        const data = await response.json();
        return data.data;
    }catch (err){
        console.log(err);
        //alert("매너 온도 조회에 실패하여 기본값으로 처리되었습니다.");
        return 20;
    }
}

function setButton(){

    // 글 작성하기 버튼
    document.querySelector(".btn-write").addEventListener("click",()=>{
        window.location.href = "/matchup/board/register";
    })

    //내가 작성한 글 목록
    document.querySelector(".btn-my-board").addEventListener("click",()=>{
        window.location.href = "/matchup/board/my";
    })

    //내가 요청한 목록
    document.querySelector(".btn-my-request").addEventListener("click",()=>{
        window.location.href = "/matchup/request/my";
    })

    //내 경기 참가 목록
    document.querySelector(".btn-my-match").addEventListener("click",()=>{
        window.location.href = "/matchup/mygame/page";
    })
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
   if(matchDate<now){
       const tds = card.querySelectorAll("td");
       tds.forEach(td =>{
           td.style.backgroundColor = "lightgray";
       })
   }

}










































