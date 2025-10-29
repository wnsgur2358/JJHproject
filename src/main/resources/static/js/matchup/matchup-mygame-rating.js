let isListenerAttached = false;
let boardId = '';
document.addEventListener("DOMContentLoaded",()=>{
    const detailDto = document.querySelector("#matchup-rating-detail-dto");
    boardId = Number(detailDto.dataset.boardId);
    autoResize();
    loadItems(1) // 프론트는 페이지 번호 시작을 1부터, 헷갈림

    const form = document.querySelector("form");
    form.addEventListener("submit", (event)=>{
            submitCheck(event)
        }
    )
})

async function loadItems(page){
    let items = [];
    let pageInfo = {
        page: 0,
        totalPages: 1
    };

    try{
        const response = await fetch(`/matchup/rating/list?page=${page-1}&boardId=${boardId}`,{

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
                <td colspan="7" class="no-result">매너 온도 평가할 대상이 없습니다.</td>
            </tr>            
            `;
        return;
    }

    items.forEach((item)=>{

        const card = document.createElement("tr");
        card.className = "matchup-card";
        card.innerHTML = `
                    <td>${item.targetName}</td>
                    <td>${setScore(item.isCompletedReceive,item.receivedMannerScore)}</td>
                    <td>${setScore(item.isCompletedReceive, item.recievedSkillScore)}</td>
                    <td><button class="received-btn">받은 후기</button></td>
                    <td>${setScore(item.isCompletedSend,item.sendedMannerScore)}</td>
                    <td>${setScore(item.isCompletedSend, item.sendedSkillScore)}</td>
                    <td><button class="sended-btn">보낸 후기</button></td>                    
                                                                        
                `;
        setSendedBtn(card,item);
        setReceivedBtn(card,item)
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


function setSendedBtn(card, item){

    const checkReviewModalEle = document.querySelector("#checkReviewModal");

    const sendReviewModalEle = document.querySelector("#sendReviewModal");
    const sendedBtn = card.querySelector(".sended-btn");

    // 내가 후기 작성을 한 경우
    if(item.isCompletedSend){

        sendedBtn.textContent = "보낸 후기";
        sendedBtn.addEventListener("click",()=>{
            checkReviewModalEle.style.display = "flex";
            document.querySelector("#check-manner-score").value = item.sendedMannerScore;
            document.querySelector("#check-skill-score").value = item.sendedSkillScore;
            document.querySelector("#check-review").textContent = item.sendedReview;
        });

    }else{
        sendedBtn.textContent = "후기 작성";
        sendedBtn.addEventListener("click",()=>{
            // window.location.href = ;
            sendReviewModalEle.style.display = "flex";
            document.querySelector("#boardId").value = item.boardId;
            document.querySelector("#evalId").value = item.sendedEvalId;
            document.querySelector("#targetId").value = item.sendedTargetId;

            document.querySelector("#sendReviewModal form").action= `/matchup/rating/register`;

        })

    }
}

function setReceivedBtn(card, item){

    const checkReviewModalEle = document.querySelector("#checkReviewModal");
    const receivedBtn = card.querySelector(".received-btn");

    // 상대가 후기 작성을 한 경우
    if(item.isCompletedReceive){
        receivedBtn.addEventListener("click",()=>{
            checkReviewModalEle.style.display = "flex";
            document.querySelector("#check-manner-score").value = item.receivedMannerScore;
            document.querySelector("#check-skill-score").value = item.recievedSkillScore;
            document.querySelector("#check-review").textContent = item.receivedReview;
        })
    }else{
        receivedBtn.classList.add("disabled");

    }
}

function closeCheckReviewModal(){
    document.querySelector("#checkReviewModal").style.display = "none";
}

function closeSendReviewModal(){
    document.querySelector("#sendReviewModal").style.display = "none";

}

function setScore(condition, score){
    if(condition){
        return score;
    }else{
        return "N";
    }
}

function setReceivedReview(item){
    if(item.isCompletedReceive){
        return item.receivedReview;
    }else{
        return "상대방이 아직 후기를 작성 안했습니다.";
    }
}

function autoResize() {
    const allTextarea = document.querySelectorAll('textarea');
    allTextarea.forEach(el =>{
        el.style.height = 'auto';  // 초기화
        el.style.height = el.scrollHeight + 'px';  // 실제 내용에 맞춤
    });
}




function goBack(){
    if (document.referrer && document.referrer !== location.href) {
        window.history.back();
    } else {
        window.location.href = "/matchup/board";
    }
    //window.location.href = "/matchup/mygame/page";
}


function submitCheck(e){


    const mannerScoreEle = document.querySelector("#mannerScore");

    const skillScoreEle = document.querySelector("#skillScore");

    const reviewEle = document.querySelector("#review");

    // 1. 리뷰 글자 수 검사
    if(isExceedCharlimit(reviewEle.value.length, 300)){
        e.preventDefault();
        //alert("리뷰는 300자 내로 작성해주세요.");
        Swal.fire({text: '리뷰는 300자 내로 작성해주세요.', icon: 'warning', confirmButtonText: '확인'});
    }


    if(mannerScoreEle.value ===""){
        //alert("매너 점수를 입력하세요.");
        Swal.fire({text: '매너 점수를 입력하세요.', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    } else if(skillScoreEle.value ===""){
        //alert("실력 점수를 입력하세요.");
        Swal.fire({text: '실력 점수를 입력하세요.', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    } else if(reviewEle.value ===""){
        //alert("리뷰를 입력하세요.");
        Swal.fire({text: '리뷰를 입력하세요.', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    } else{
        //alert("매너 후기가 전송되었습니다.");
        //Swal.fire({text: '매너 후기가 전송되었습니다.', icon: 'warning', confirmButtonText: '확인'});

        e.preventDefault(); // 기본 제출 막기
        Swal.fire({
            text: '매너 후기가 전송되었습니다.',
            icon: 'success',
            confirmButtonText: '확인'
        }).then((result) => {
            if (result.isConfirmed) {
                // 버튼에서 올라가며 가장 가까운 form 찾기
                e.target.closest("form").submit();
            }
        });
    }
}

function isExceedCharlimit(length, limit){

    if(Number(length)>Number(limit))
        return true;
    else
        return false;
}

