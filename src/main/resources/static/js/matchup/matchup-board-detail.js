document.addEventListener("DOMContentLoaded",()=>{
        setContent();
})


async function setContent(){
    const detailDto = document.querySelector("#matchup-board-detail-dto");

    const writerEmail = detailDto.dataset.writerEmail;
    const sportsFacilityName = detailDto.dataset.sportsFacilityName;
    const sportsFacilityAddress = detailDto.dataset.sportsFacilityAddress;
    const matchDatetime = detailDto.dataset.matchDatetime;
    const matchEndtime = detailDto.dataset.matchEndtime;
    const currentParticipantCount = Number(detailDto.dataset.currentParticipantCount);
    const maxParticipants = Number(detailDto.dataset.maxParticipants);
    const minMannerTemperature = Number(detailDto.dataset.minMannerTemperature);
    const savedName = detailDto.dataset.savedName;
    const myMannerTemperature = Number(detailDto.dataset.myMannerTemperature)||20;
    const loginEmail = detailDto.dataset.loginEmail;
    //const baseUrl = detailDto.dataset.baseUrl;
    //console.log(sportsFacilityAddress);
    //console.log(matchDatetime);
    //console.log(loginMemberName);

    drawMap(sportsFacilityAddress, sportsFacilityName);
    calTime(matchDatetime, matchEndtime);
    checkStatus(matchDatetime, matchEndtime, currentParticipantCount, maxParticipants, writerEmail, loginEmail, minMannerTemperature, myMannerTemperature);
    setButton(matchDatetime, writerEmail, loginEmail, currentParticipantCount, maxParticipants, minMannerTemperature, myMannerTemperature);



    try{
        const response = await fetch(`/matchup/attachment/presigned-url?saved-name=${savedName}`,{
            method: "GET",
            credentials: "include"
        })
        if(!response.ok)
            throw new Error(`HTTP error! Status:${response.status}`)
        const data = await response.json();
        //console.log(data.data);
        document.querySelector("#reservationUrl").addEventListener("click",()=>{
            window.open(data.data,"_blank");
        });
    }catch (err){
        console.log(err);
    }

    //아래는 CORS 막아놔서 안됨
    // const response2 = await fetch(data.data);
    // if(!response2.ok)
    //     throw new Error(`HTTP error! Status:${response.status}`)
    // const data2 = await response2.json();
    // console.log(data2);



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
    const endDate = new Date(matchEndtime);
    //console.log(date);
    const matchDateEle = document.querySelector("#match-date");

    const month = date.getMonth()+1;
    const day = date.getDate();

    const startHour = date.getHours();
    const startMinutes = date.getMinutes();

    const endHour = endDate.getHours();
    const endMinute = endDate.getMinutes();


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

    matchDateEle.value = `${month}/${day} ${startHour}시 ${startMinutes}분 - ${endHour}시 ${endMinute}분`

}


function checkStatus(matchDatetime, matchEndtime, currentParticipantCount, maxParticipants, writerEmail, loginEmail, minMannerTemperature, myMannerTemperature){
    const statusEle = document.querySelector("#status");

    // console.log(currentParticipantCount)
    // console.log(maxParticipants)

    // 공통: 경기 날짜 지나면 경기종료
    const matchDate = new Date(matchDatetime);
    const endMatchDate = new Date(matchEndtime);
    const now = new Date();
    // const durationParts = matchDuration.split(":");
    // const matchEnd = new Date(matchDate.getTime() + (parseInt(durationParts[0])*60+parseInt(durationParts[1])) * 60 * 1000);

    if(matchDate <now && now <= endMatchDate){
        statusEle.value = "경기 진행";
    }else if(endMatchDate<now){
        statusEle.value = "경기 종료";
    }else if(writerEmail === loginEmail){
        // 경우1: 사용자가 쓴 글
        // 신청 가능 여부: 모집 중, 모집 완료, 경기 종료

        if(currentParticipantCount < maxParticipants)
            statusEle.value =  "모집 가능";
        else
            statusEle.value = "모집 완료";
    }else{
        // 경우2: 다른 사람의 글
        // 신청 가능 여부: 신청 가능, 신청 마감, 입장 불가, 경기 종료
        if(minMannerTemperature>myMannerTemperature)
            statusEle.value = "입장 불가";
        else if(currentParticipantCount < maxParticipants)
            statusEle.value = "신청 가능";
        else
            statusEle.value = "신청 불가";
    }
}

function setButton(matchDatetime, writerEmail, loginEmail,currentParticipantCount, maxParticipants, minMannerTemperature, myMannerTemperature ){
    const matchDate = new Date(matchDatetime);
    // const now = new Date();
    // console.log(now);
    // console.log(matchDate<now)
    // console.log(matchDate>now)

    if(writerEmail === loginEmail){ //수정하기 버튼, 삭제하기 버튼
       const modifyBtn = document.querySelector("#modifyBtn");
       const deleteBtn = document.querySelector("#deleteBtn");

        modifyBtn.addEventListener("click",(e)=>{
            const now = new Date();
            if(matchDate<=now){
                e.preventDefault();
                modifyBtn.href = "#";
                //alert("경기 시작 시간이 지나 수정할 수 없습니다.");
                Swal.fire({text: '경기 시작 시간이 지나 수정할 수 없습니다.', icon: 'warning', confirmButtonText: '확인'});
            }
       });

        deleteBtn.addEventListener("click",(e)=>{
            const now = new Date();
            if(matchDate<=now){
                e.preventDefault();
                deleteBtn.href = "#";
                //alert("경기 시작 시간이 지나 삭제할 수 없습니다.");
                Swal.fire({text: '경기 시작 시간이 지나 삭제할 수 없습니다.', icon: 'warning', confirmButtonText: '확인'});
            }
        });
    } else if(writerEmail !==loginEmail){ // 문의하기, 참가요청 버튼
        const chatBtn = document.querySelector("#chat1-1Btn");
        const requestBtn = document.querySelector("#requestBtn");

        chatBtn.addEventListener("click",(e)=>{
            const now = new Date();
            if(matchDate<=now){
                e.preventDefault();
                chatBtn.href = "#";
                //alert("경기 시작 시간이 지나 1대1 문의를 할 수 없습니다.")
                Swal.fire({text: '경기 시작 시간이 지나 1대1 문의를 할 수 없습니다.', icon: 'warning', confirmButtonText: '확인'});
            }
        });

        requestBtn.addEventListener("click",(e)=>{
            const now = new Date();
            if(matchDate<=now){
                e.preventDefault();
                requestBtn.href = "#";
                //alert("경기 시작 시간이 지나 참가 요청을 할 수 없습니다.")
                Swal.fire({text: '경기 시작 시간이 지나 참가 요청을 할 수 없습니다.', icon: 'warning', confirmButtonText: '확인'});
            }else if(minMannerTemperature>myMannerTemperature){  // 매너 온도 충족 안된 경우
                e.preventDefault();
                requestBtn.href = "#";
                //alert("입장 가능 매너 온도를 충족하지 못해 신청이 불가능합니다. 작성자에게 1:1 문의해보세요.")
                Swal.fire({text: '입장 가능 매너 온도를 충족하지 못해 신청이 불가능합니다. 작성자에게 1:1 문의해보세요.', icon: 'warning', confirmButtonText: '확인'});
            }else if(currentParticipantCount >=maxParticipants) {  // 참가 인원 다 찬 경우
                e.preventDefault();
                chatBtn.href = "#";
                //alert("현재 참가 요청 인원이 다 모집되어, 신청이 불가능합니다. 작성자에게 1:1 문의해보세요.")
                Swal.fire({text: '현재 참가 요청 인원이 다 모집되어, 신청이 불가능합니다. 작성자에게 1:1 문의해보세요.', icon: 'warning', confirmButtonText: '확인'});
            }
        });

    }
}

function goBack(){
    if (document.referrer && document.referrer !== location.href) {
        window.history.back();
    } else {
        window.location.href = "/matchup/board";
    }
}
























