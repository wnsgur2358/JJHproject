const Status = {
    PENDING: "PENDING",
    APPROVED: "APPROVED",
    DENIED: "DENIED",
    CANCELREQUESTED: "CANCELREQUESTED"
}

document.addEventListener("DOMContentLoaded",()=>{
    const registerDto = document.querySelector("#matchup-request-register-dto");

    const sportsTypeName = registerDto.dataset.sportsTypeName;
    const sportsFacilityName = registerDto.dataset.sportsFacilityName;
    const sportsFacilityAddress = registerDto.dataset.sportsFacilityAddress;
    const matchDatetime = registerDto.dataset.matchDatetime;
    const matchEndtime = registerDto.dataset.matchEndtime;
    const currentParticipantCount = Number(registerDto.dataset.currentParticipantCount);
    const maxParticipants = Number(registerDto.dataset.maxParticipants);
    const participantCount = Number(registerDto.dataset.participantCount);

    const matchupStatus = registerDto.dataset.matchupStatus;
    const matchupRequestSubmittedCount = Number(registerDto.dataset.matchupRequestSubmittedCount);
    const matchupCancelSubmittedCount = Number(registerDto.dataset.matchupCancelSubmittedCount);
    const isDeleted = registerDto.dataset.isDeleted  === "true";


    drawMap(sportsFacilityAddress, sportsFacilityName);
    calTime(matchDatetime, matchEndtime);
    setParticipantCount(currentParticipantCount, maxParticipants, participantCount);
    manageRequestInfo(matchupStatus, matchupRequestSubmittedCount, matchupCancelSubmittedCount, isDeleted, matchDatetime);
    autoResize();

    const form = document.querySelector("#request-info");
    form.addEventListener("submit",(e)=>{
        submitCheck(e, matchDatetime);
    })


})

function submitCheck(e, matchDatetime){
    const selfIntroEle = document.querySelector("#selfIntro");
    const participantCountEle = document.querySelector("#participantCount");
    const date = new Date(matchDatetime);
    const now = new Date();

    // 1. 자기소개 글자 수 검사
    if(isExceedCharlimit(selfIntroEle.value.length, 300)){
        e.preventDefault();
        //alert("자기소개는 300자 내로 작성해주세요.");
        Swal.fire({text: '자기소개는 300자 내로 작성해주세요.', icon: 'warning', confirmButtonText: '확인'});
    }

    if(selfIntroEle.value ===""){
        //alert("자기 소개를 입력하세요.");
        Swal.fire({text: '자기 소개를 입력하세요.', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    }else if(participantCountEle.value === ""){
        //alert("참가 인원을 입력하세요.");
        Swal.fire({text: '참가 인원을 입력하세요.', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    }else if(date<=now){
        e.preventDefault();
        //alert("경기 시작 시간이 지나 수정할 수 없습니다.");
        Swal.fire({text: '경기 시작 시간이 지나 수정할 수 없습니다.', icon: 'warning', confirmButtonText: '확인'});
    }else{
        //alert("요청 수정이 완료되었습니다.");
        //Swal.fire({text: '요청 수정이 완료되었습니다.', icon: 'success', confirmButtonText: '확인'});

        e.preventDefault(); // 기본 제출 막기
        Swal.fire({
            text: '요청 수정이 완료되었습니다.',
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
    // console.log(matchDatetime);
    // console.log(matchDuration);

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


function setParticipantCount(currentParticipantCount, maxParticipants, participantCount){

    const participantCountEle = document.querySelector("#participantCount");

    for(let i=1; i<=(maxParticipants-currentParticipantCount);i++){
        const option = document.createElement("option");
        option.value = i;
        option.textContent = i;
        if(i === participantCount)
            option.selected = true;
        participantCountEle.appendChild(option);

    }
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
}

function isExceedCharlimit(length, limit){

    if(Number(length)>Number(limit))
        return true;
    else
        return false;
}