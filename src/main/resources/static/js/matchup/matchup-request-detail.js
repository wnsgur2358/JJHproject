const Status = {
    PENDING: "PENDING",
    APPROVED: "APPROVED",
    DENIED: "DENIED",
    CANCELREQUESTED: "CANCELREQUESTED"
}

document.addEventListener("DOMContentLoaded",()=>{
    setContent();
})




function setContent(){
    const detailDto = document.querySelector("#matchup-request-detail-dto");

    const boardId = detailDto.dataset.boardId;
    const requestId = detailDto.dataset.requestId;
    const sportsFacilityName = detailDto.dataset.sportsFacilityName;
    const sportsFacilityAddress = detailDto.dataset.sportsFacilityAddress;
    const matchDatetime = detailDto.dataset.matchDatetime;
    const matchEndtime = detailDto.dataset.matchEndtime;
    const matchupStatus = detailDto.dataset.matchupStatus;
    const matchupRequestSubmittedCount = Number(detailDto.dataset.matchupRequestSubmittedCount);
    const matchupCancelSubmittedCount = Number(detailDto.dataset.matchupCancelSubmittedCount);
    const isDeleted = detailDto.dataset.isDeleted === "true";
    const currentParticipantCount = Number(detailDto.dataset.currentParticipantCount);
    const maxParticipants = Number(detailDto.dataset.maxParticipants);
    const participantCount = Number(detailDto.dataset.participantCount);
    const boardWriterEmail = detailDto.dataset.boardWriterEmail;
    const applicantEmail = detailDto.dataset.applicantEmail;
    const loginMemberEmail = detailDto.dataset.loginMemberEmail;



    drawMap(sportsFacilityAddress, sportsFacilityName);
    calTime(matchDatetime, matchEndtime);
    manageRequestInfo(matchupStatus, matchupRequestSubmittedCount, matchupCancelSubmittedCount, isDeleted, matchDatetime);

   if(applicantEmail === loginMemberEmail)
        setUserButton(matchupStatus, matchupRequestSubmittedCount, matchupCancelSubmittedCount, isDeleted, matchDatetime)




}
function drawMap(address, sportsFacilityName){
    var mapContainer = document.getElementById('map'), // 지도를 표시할 div
        mapOption = {
            center: new kakao.maps.LatLng(33.450701, 126.570667), // 지도의 중심좌표
            level: 3 // 지도의 확대 레벨
        };

// 지도를 생성합니다
    var map = new kakao.maps.Map(mapContainer, mapOption);

// 주소-좌표 변환 객체를 생성합니다
    var geocoder = new kakao.maps.services.Geocoder();

// 주소로 좌표를 검색합니다
    geocoder.addressSearch(address, function(result, status) {

        // 정상적으로 검색이 완료됐으면
        if (status === kakao.maps.services.Status.OK) {

            var coords = new kakao.maps.LatLng(result[0].y, result[0].x);

            // 결과값으로 받은 위치를 마커로 표시합니다
            var marker = new kakao.maps.Marker({
                map: map,
                position: coords
            });

            // 인포윈도우로 장소에 대한 설명을 표시합니다
            var infowindow = new kakao.maps.InfoWindow({
                content: '<div class="truncateMap" style="width:150px;text-align:center;padding:6px 0;">'+sportsFacilityName+'</div>'
            });
            infowindow.open(map, marker);

            // 지도의 중심을 결과값으로 받은 위치로 이동시킵니다
            map.setCenter(coords);
        }
    });
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

    matchDateEle.value = `${month}/${day} ${startHour}시 ${startMinutes}분 - ${endHour}시 ${endMinutes}분`

}


function manageRequestInfo(matchupStatus, matchupRequestSubmittedCount, matchupCancelSubmittedCount, isDeleted, matchDatetime){
    const matchDate = new Date(matchDatetime);
    const now = new Date();

    //console.log(matchDate<now);

    const statusEle = document.querySelector("#status");

    // console.log(matchupStatus);
    // console.log(matchupRequestSubmittedCount);
    // console.log(matchupCancelSubmittedCount);
    // console.log(isDeleted);
    // if(matchupStatus === Status.PENDING)
    //     console.log("enum 사용");
    // console.log(matchupStatus ===Status.PENDING && matchupRequestSubmittedCount===1 && matchupCancelSubmittedCount===0 && isDeleted===false);

    // 1. 참가 요청 후 승인 대기
    if(
        (matchupStatus ===Status.PENDING && matchupRequestSubmittedCount===1 && matchupCancelSubmittedCount===0 && isDeleted===false) ||
        (matchupStatus===Status.PENDING && matchupRequestSubmittedCount===2 && matchupCancelSubmittedCount===0 && isDeleted ===false)
    ){
        statusEle.value =  "승인 대기";
    }
    // 2. 참가 요청 삭제
    else if(
        (matchupStatus===Status.PENDING && matchupRequestSubmittedCount===1 && matchupCancelSubmittedCount===0 && isDeleted===true) ||
        (matchupStatus===Status.PENDING && matchupRequestSubmittedCount===2 && matchupCancelSubmittedCount===0 && isDeleted===true)
    ){
        statusEle.value =  "요청 취소됨";
    }
    // 3. 참가 요청 승인
    else if(
        (matchupStatus===Status.APPROVED && matchupRequestSubmittedCount===1 && matchupCancelSubmittedCount===0 && isDeleted===false)||
        (matchupStatus===Status.APPROVED && matchupRequestSubmittedCount===2 && matchupCancelSubmittedCount===0 && isDeleted===false)
    ){
        statusEle.value = "승인됨";
    }
    // 4. 참가 요청 반려
    else if(
        (matchupStatus === Status.DENIED && matchupRequestSubmittedCount ===1 && matchupCancelSubmittedCount ===0 && isDeleted ===false) ||
        (matchupStatus === Status.DENIED && matchupRequestSubmittedCount ===2 && matchupCancelSubmittedCount ===0 && isDeleted ===false)
    ){
        statusEle.value = "반려됨";
    }
    // 8. 승인 취소 요청을 했으나 경기 시간이 지나 자동 참가 처리
    else if(
        (matchDate <now) &&
        (
            (matchupStatus === Status.CANCELREQUESTED && matchupRequestSubmittedCount ===2 && matchupCancelSubmittedCount ===1 && isDeleted===false) ||
            (matchupStatus === Status.CANCELREQUESTED && matchupRequestSubmittedCount ===1 && matchupCancelSubmittedCount ===1 && isDeleted===false)
        )
    ){
        statusEle.value = "자동 참가"
    }
    // 5. 승인 취소 요청 상태
    else if(
        (matchupStatus === Status.CANCELREQUESTED && matchupRequestSubmittedCount ===2 && matchupCancelSubmittedCount ===1 && isDeleted===false) ||
        (matchupStatus === Status.CANCELREQUESTED && matchupRequestSubmittedCount ===1 && matchupCancelSubmittedCount ===1 && isDeleted===false)
    ){
        statusEle.value = "승인 취소 요청";
    }
    // 6. 승인 취소 요청이 승인
    else if(
        (matchupStatus===Status.CANCELREQUESTED && matchupRequestSubmittedCount === 2 && matchupCancelSubmittedCount===1 && isDeleted===true) ||
        (matchupStatus===Status.CANCELREQUESTED && matchupRequestSubmittedCount === 1 && matchupCancelSubmittedCount===1 && isDeleted===true)
    ){
        statusEle.value = "취소 요청 승인";
    }
    // 7. 승인 취소 요청이 반려
    else if(
        (matchupStatus===Status.APPROVED && matchupRequestSubmittedCount===2 && matchupCancelSubmittedCount===1 && isDeleted ===false) ||
        (matchupStatus===Status.APPROVED && matchupRequestSubmittedCount===1 && matchupCancelSubmittedCount===1 && isDeleted ===false)
    ){
        statusEle.value = "취소 요청 반려";
    }else{
        statusEle.value = "서버 오류";
    }
}


//  작성자 승인 및 반려
// function setDecision(matchupStatus, matchupRequestSubmittedCount, matchupCancelSubmittedCount, isDeleted, matchDatetime, boardId, requestId, currentParticipantCount, maxParticipants, participantCount) {
//     const approvedBtn = document.querySelector("#approvedBtn");
//     const deniedBtn = document.querySelector("#deniedBtn");
//     const approveWithdrawRequestBtn = document.querySelector("#approveWithdrawRequestBtn");
//     const denyWithdrawRequestBtn = document.querySelector("#denyWithdrawRequestBtn");
//
//     // 클릭 시 체크 날짜, 인원수 --> DB에서도 체크해야됨
//     const matchDate = new Date(matchDatetime);
//     let now = new Date();
//
//     approvedBtn.addEventListener("click",(e)=>{
//         now = new Date();
//         if(matchDate<now){
//             e.preventDefault();
//             e.target.href = "#";
//             e.target.classList.add("disabled");
//             alert("경기 시작 시간이 지나 승인을 할 수 없습니다.");
//         }
//     });
//
//     deniedBtn.addEventListener("click",(e)=>{
//         now = new Date();
//         if(matchDate<now){
//             e.preventDefault();
//             e.target.href = "#";
//             e.target.classList.add("disabled");
//             alert("경기 시작 시간이 지나 반려를 할 수 없습니다.");
//         }
//     })
//
//     approveWithdrawRequestBtn.addEventListener("click",(e)=>{
//         now = new Date();
//         if(matchDate<now){
//             e.preventDefault();
//             e.target.href = "#";
//             e.target.classList.add("disabled");
//             alert("경기 시작 시간이 지나 취소 요청 승인을 할 수 없습니다.");
//         }
//     })
//
//     denyWithdrawRequestBtn.addEventListener("click",(e)=>{
//         now = new Date();
//         if(matchDate<now){
//             e.preventDefault();
//             e.target.href = "#";
//             e.target.classList.add("disabled");
//             alert("경기 시작 시간이 지나 취소 요청 반려를 할 수 없습니다.");
//         }
//     })
//
//     // 참가 요청에 대한 승인/반려
//     if(
//         (matchupStatus===Status.PENDING && matchupRequestSubmittedCount===1 && matchupCancelSubmittedCount ===0 && isDeleted===false)||
//         (matchupStatus===Status.PENDING && matchupRequestSubmittedCount===2 && matchupCancelSubmittedCount ===0 && isDeleted===false)
//     ){
//         // approvedBtn.href = `/matchup/request/approve?request-id=${requestId}&board-id=${boardId}`;
//         // deniedBtn.href = `/matchup/request/deny?request-id=${requestId}&board-id=${boardId}`;
//
//         // 참가 승인 가능
//         // 참가 반려 가능
//
//         //취소 승인 불가능
//         approveWithdrawRequestBtn.classList.add("disabled");
//         approveWithdrawRequestBtn.href = "#";
//
//         //취소 반려 불가능
//
//         denyWithdrawRequestBtn.classList.add("disabled");
//         denyWithdrawRequestBtn.href = "#";
//
//     }
//     //승인 취소 요청에 대한 승인/반려
//     else if(
//         (matchupStatus===Status.CANCELREQUESTED && matchupRequestSubmittedCount===1 && matchupCancelSubmittedCount ===1 && isDeleted===false) ||
//         (matchupStatus===Status.CANCELREQUESTED && matchupRequestSubmittedCount===2 && matchupCancelSubmittedCount ===1 && isDeleted===false)
//     ){
//         // approvedBtn.href = `/matchup/request/approve?request-id=${requestId}&board-id=${boardId}`;
//         // deniedBtn.href = `/matchup/request/deny?request-id=${requestId}&board-id=${boardId}`;
//
//         // 참가 승인 불가능
//         approvedBtn.classList.add("disabled");
//         approvedBtn.href = "#";
//
//         // 참가 반려 불가능
//         deniedBtn.classList.add("disabled");
//         deniedBtn.href = "#";
//
//         //취소 승인 가능
//
//         //취소 반려 가능
//
//     }else{
//         // 예측하지 못한 오류 발생한 경우
//
//         // 참가 승인 불가능
//         approvedBtn.classList.add("disabled");
//         approvedBtn.href = "#";
//
//         // 참가 반려 불가능
//         deniedBtn.classList.add("disabled");
//         deniedBtn.href = "#";
//
//         //취소 승인 불가능
//         approveWithdrawRequestBtn.classList.add("disabled");
//         approveWithdrawRequestBtn.href = "#";
//
//         //취소 반려 불가능
//         denyWithdrawRequestBtn.classList.add("disabled");
//         denyWithdrawRequestBtn.href = "#";
//     }
//
//     // 마지막에 인원수 체크, 승인 버튼 없애기
//     if(currentParticipantCount+participantCount>maxParticipants){
//
//         // 참가 승인 불가능
//         approvedBtn.classList.add("disabled");
//         approvedBtn.href = "#";
//
//         // 참가 반려 불가능
//
//         // 취소 승인 가능
//
//         // 취소 반려 가능
//
//     }
// }

function setUserButton(matchupStatus, matchupRequestSubmittedCount, matchupCancelSubmittedCount, isDeleted, matchDatetime) {

    const modifyBtn = document.querySelector("#modifyBtn");
    const cancelBtn = document.querySelector("#cancelBtn");
    const retryBtn = document.querySelector("#retryBtn");
    const withdrawBtn = document.querySelector("#withdrawBtn");

    const matchDate = new Date(matchDatetime);
    let now = new Date();

    modifyBtn.addEventListener("click",(e)=>{

        now = new Date();
        if(matchDate<=now){
            e.preventDefault();
            e.target.href = "#";
            e.target.classList.add("disabled");
            //alert("경기 시작 시간이 지나 수정할 수 없습니다.");
            Swal.fire({text: '경기 시작 시간이 지나 수정할 수 없습니다.', icon: 'warning', confirmButtonText: '확인'});
        }
    });

    cancelBtn.addEventListener("click",(e)=>{
        now = new Date();
        if(matchDate<=now){
            e.preventDefault();
            e.target.href = "#";
            e.target.classList.add("disabled");
            //alert("경기 시작 시간이 지나 취소할 수 없습니다.");
            Swal.fire({text: '경기 시작 시간이 지나 취소할 수 없습니다.', icon: 'warning', confirmButtonText: '확인'});
        }
    });

    retryBtn.addEventListener("click",(e)=>{
        now = new Date();
        if(matchDate<=now){
            e.preventDefault();
            e.target.href = "#";
            e.target.classList.add("disabled");
            //alert("경기 시작 시간이 지나 재 요청할 수 없습니다.");
            Swal.fire({text: '경기 시작 시간이 지나 재 요청할 수 없습니다.', icon: 'warning', confirmButtonText: '확인'});
        }
    });

    withdrawBtn.addEventListener("click",(e)=>{
        now = new Date();
        if(matchDate<=now){
            e.preventDefault();
            e.target.href = "#";
            e.target.classList.add("disabled");
            //alert("경기 시작 시간이 지나 승인 취소 요청할 수 없습니다.");
            Swal.fire({text: '경기 시작 시간이 지나 승인 취소 요청할 수 없습니다.', icon: 'warning', confirmButtonText: '확인'});
        }
    });

    // 1. 참가 요청 후 승인 대기
    if(
        (matchupStatus ===Status.PENDING && matchupRequestSubmittedCount===1 && matchupCancelSubmittedCount===0 && isDeleted===false) ||
        (matchupStatus===Status.PENDING && matchupRequestSubmittedCount===2 && matchupCancelSubmittedCount===0 && isDeleted ===false)
    ){
        // 수정하기 가능
        // 요청 취소 가능

        //재 요청 불가능
        retryBtn.classList.add("disabled");
        retryBtn.href = "#";

        //승인 취소 요청 불가능
        withdrawBtn.classList.add("disabled");
        withdrawBtn.href = "#";
    }
    // 2.1 참가 요청 취소- 재요청 가능
    else if(
        (matchupStatus===Status.PENDING && matchupRequestSubmittedCount===1 && matchupCancelSubmittedCount===0 && isDeleted===true)

    ){
        // 수정하기 불가능
        modifyBtn.classList.add("disabled");
        modifyBtn.href= "#";

        // 요청 취소 불가능
        cancelBtn.classList.add("disabled");
        cancelBtn.href = "#";

        //재 요청 가능

        //승인 취소 요청 불가능
        withdrawBtn.classList.add("disabled");
        withdrawBtn.href = "#";
    }
    // 2.2 참가 요청 취소- 재요청 불가능
    else if( (matchupStatus===Status.PENDING && matchupRequestSubmittedCount===2 && matchupCancelSubmittedCount===0 && isDeleted===true)){

        // 수정하기 불가능
        modifyBtn.classList.add("disabled");
        modifyBtn.href = "#";

        // 요청 취소 불가능
        cancelBtn.classList.add("disabled");
        cancelBtn.href = "#";

        //재 요청 가능
        retryBtn.classList.add("disabled");
        retryBtn.href = "#";

        //승인 취소 요청 불가능
        withdrawBtn.classList.add("disabled");
        withdrawBtn.href = "#";

    }
    // 3. 참가 요청 승인
    else if(
        (matchupStatus===Status.APPROVED && matchupRequestSubmittedCount===1 && matchupCancelSubmittedCount===0 && isDeleted===false)||
        (matchupStatus===Status.APPROVED && matchupRequestSubmittedCount===2 && matchupCancelSubmittedCount===0 && isDeleted===false)
    ){
        // 수정하기 불가능
        modifyBtn.classList.add("disabled");
        modifyBtn.href = "#";

        // 요청 취소 불가능
        cancelBtn.classList.add("disabled");
        cancelBtn.href = "#";

        //재 요청 불가능
        retryBtn.classList.add("disabled");
        retryBtn.href = "#";

        //승인 취소 요청 가능
    }
    // 4.1 참가 요청 반려- 재요청가능
    else if(
        (matchupStatus === Status.DENIED && matchupRequestSubmittedCount ===1 && matchupCancelSubmittedCount ===0 && isDeleted ===false)

    ){
        // 수정하기 불가능
        modifyBtn.classList.add("disabled");
        modifyBtn.href = "#";
        // 요청 취소 불가능
        cancelBtn.classList.add("disabled");
        cancelBtn.href = "#";
        //재 요청 가능

        //승인 취소 요청 불가능
        withdrawBtn.classList.add("disabled");
        withdrawBtn.href = "#";

    }
    // 4.2 참가 요청 반려- 재요청불가능
    else if((matchupStatus === Status.DENIED && matchupRequestSubmittedCount ===2 && matchupCancelSubmittedCount ===0 && isDeleted ===false)){
        // 수정하기 불가능
        modifyBtn.classList.add("disabled");
        modifyBtn.href = "#";

        // 요청 취소 불가능
        cancelBtn.classList.add("disabled");
        cancelBtn.href = "#";

        //재 요청 가능
        retryBtn.classList.add("disabled");
        retryBtn.href = "#";

        //승인 취소 요청 불가능
        withdrawBtn.classList.add("disabled");
        withdrawBtn.href = "#";
    }
    // 8. 승인 취소 요청을 했으나 경기 시간이 지나 자동 참가 처리
    else if(
        (matchDate < now) &&
        (
            (matchupStatus === Status.CANCELREQUESTED && matchupRequestSubmittedCount ===2 && matchupCancelSubmittedCount ===1 && isDeleted===false) ||
            (matchupStatus === Status.CANCELREQUESTED && matchupRequestSubmittedCount ===1 && matchupCancelSubmittedCount ===1 && isDeleted===false)
        )
    ){
        // 수정하기 불가능
        modifyBtn.classList.add("disabled");
        modifyBtn.href = "#";

        // 요청 취소 불가능
        cancelBtn.classList.add("disabled");
        cancelBtn.href = "#";

        //재 요청 가능
        retryBtn.classList.add("disabled");
        retryBtn.href = "#";

        //승인 취소 요청 불가능
        withdrawBtn.classList.add("disabled");
        withdrawBtn.href = "#";
    }
    // 5. 승인 취소 요청 상태
    else if(
        (matchupStatus === Status.CANCELREQUESTED && matchupRequestSubmittedCount ===2 && matchupCancelSubmittedCount ===1 && isDeleted===false) ||
        (matchupStatus === Status.CANCELREQUESTED && matchupRequestSubmittedCount ===1 && matchupCancelSubmittedCount ===1 && isDeleted===false)
    ){
        // 수정하기 불가능
        modifyBtn.classList.add("disabled");
        modifyBtn.href = "#";

        // 요청 취소 불가능
        cancelBtn.classList.add("disabled");
        cancelBtn.href = "#";

        //재 요청 가능
        retryBtn.classList.add("disabled");
        retryBtn.href = "#";

        //승인 취소 요청 불가능
        withdrawBtn.classList.add("disabled");
        withdrawBtn.href = "#";
    }
    // 6. 승인 취소 요청이 승인
    else if(
        (matchupStatus===Status.CANCELREQUESTED && matchupRequestSubmittedCount === 2 && matchupCancelSubmittedCount===1 && isDeleted===true) ||
        (matchupStatus===Status.CANCELREQUESTED && matchupRequestSubmittedCount === 1 && matchupCancelSubmittedCount===1 && isDeleted===true)
    ){
        // 수정하기 불가능
        modifyBtn.classList.add("disabled");
        modifyBtn.href = "#";

        // 요청 취소 불가능
        cancelBtn.classList.add("disabled");
        cancelBtn.href = "#";

        //재 요청 가능
        retryBtn.classList.add("disabled");
        retryBtn.href = "#";

        //승인 취소 요청 불가능
        withdrawBtn.classList.add("disabled");
        withdrawBtn.href = "#";
    }
    // 7. 승인 취소 요청이 반려
    else if(
        (matchupStatus===Status.APPROVED && matchupRequestSubmittedCount===2 && matchupCancelSubmittedCount===1 && isDeleted ===false) ||
        (matchupStatus===Status.APPROVED && matchupRequestSubmittedCount===1 && matchupCancelSubmittedCount===1 && isDeleted ===false)
    ){
        // 수정하기 불가능
        modifyBtn.classList.add("disabled");
        modifyBtn.href = "#";

        // 요청 취소 불가능
        cancelBtn.classList.add("disabled");
        cancelBtn.href = "#";

        //재 요청 가능
        retryBtn.classList.add("disabled");
        retryBtn.href = "#";

        //승인 취소 요청 불가능
        withdrawBtn.classList.add("disabled");
        withdrawBtn.href = "#";

    }else{
        // 예측하지 못한 오류 발생

        // 수정하기 불가능
        modifyBtn.classList.add("disabled");
        modifyBtn.href = "#";

        // 요청 취소 불가능
        cancelBtn.classList.add("disabled");
        cancelBtn.href = "#";

        //재 요청 가능
        retryBtn.classList.add("disabled");
        retryBtn.href = "#";

        //승인 취소 요청 불가능
        withdrawBtn.classList.add("disabled");
        withdrawBtn.href = "#";
    }
}

function goBack(){
    if (document.referrer && document.referrer !== location.href) {
        window.history.back();
    } else {
        window.location.href = "/matchup/board";
    }
}


