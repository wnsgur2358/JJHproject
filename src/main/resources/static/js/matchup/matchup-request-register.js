document.addEventListener("DOMContentLoaded",()=>{
    const registerDto = document.querySelector("#matchup-request-register-dto");

    const sportsFacilityName = registerDto.dataset.sportsFacilityName;
    const sportsFacilityAddress = registerDto.dataset.sportsFacilityAddress;
    const matchDatetime = registerDto.dataset.matchDatetime;
    const matchEndtime = registerDto.dataset.matchEndtime;
    const currentParticipantCount = Number(registerDto.dataset.currentParticipantCount);
    const maxParticipants = Number(registerDto.dataset.maxParticipants);


    drawMap(sportsFacilityAddress, sportsFacilityName);
    calTime(matchDatetime, matchEndtime);
    setParticipantCount(currentParticipantCount, maxParticipants);

    const form = document.querySelector("#request-info");
    form.addEventListener("submit", (e)=>{
            submitCheck(e, matchDatetime);
        }
    )

    const cancelBtn = document.querySelector(".cancel-btn");
    cancelBtn.addEventListener("click",()=>{
        history.back();
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
        //alert("경기 시작 시간이 지나 등록할 수 없습니다.");
        Swal.fire({text: '경기 시작 시간이 지나 등록할 수 없습니다.', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    }else{
        //lert("요청 등록이 완료되었습니다.");
        //Swal.fire({text: '요청 등록이 완료되었습니다.', icon: 'success', confirmButtonText: '확인'});
        e.preventDefault(); // 기본 제출 막기
        Swal.fire({
            text: '요청 등록이 완료되었습니다.',
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


function setParticipantCount(currentParticipantCount, maxParticipants){

    const participantCountEle = document.querySelector("#participantCount");

    for(let i=1; i<=(maxParticipants-currentParticipantCount);i++){
        const option = document.createElement("option");
        option.value = i;
        option.textContent = i;
        participantCountEle.appendChild(option);
        if(i===1)
            option.selected = true;
    }
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