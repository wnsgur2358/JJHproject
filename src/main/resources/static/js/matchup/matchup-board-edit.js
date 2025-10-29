

document.addEventListener("DOMContentLoaded",()=>{

    const editDto = document.querySelector("#matchup-board-edit-dto");

    const sportsTypeName = editDto.dataset.sportsTypeName;
    const currentParticipantCount = Number(editDto.dataset.currentParticipantCount);
    const maxParticipants = Number(editDto.dataset.maxParticipants);
    const minMannerTemperature = Number(editDto.dataset.minMannerTemperature);
    const originalName = editDto.dataset.originalName;
    const savedName = editDto.dataset.savedName;
    const myMannerTemperature = Number(editDto.dataset.myMannerTemperature);
    const matchDatetime = editDto.dataset.matchDatetime;
    const matchEndtime = editDto.dataset.matchEndtime;

    // console.log(matchDatetime)
    // console.log(matchDuration)


    setSportsType(sportsTypeName); // 종목 가져옴
    void setReservationFile(originalName, savedName);
    setDate(matchDatetime, matchEndtime);
    setMaxParticipants(currentParticipantCount, maxParticipants);
    setMannerTemperature(minMannerTemperature, myMannerTemperature);
    setButton();
    autoResize();

    const form = document.querySelector("form");
    form.addEventListener("submit", (e)=>{
        submitCheck(e, myMannerTemperature);
    })

})

function submitCheck(e, myMannerTemperature){

    const sportsTypeNameEle = document.querySelector("#sportsTypeName");

    const teamNameEle = document.querySelector("#teamName");

    const teamIntroEle = document.querySelector("#teamIntro");

    const sportsFacilityNameEle = document.querySelector("#sportsFacilityName");

    const sportsFacilityAddressEle = document.querySelector("#sportsFacilityAddress");

    const currentParticipantCountEle = document.querySelector("#currentParticipantCount");
    //console.log(currentParticipantsCountEle.value);

    const maxParticipantsEle = document.querySelector("#maxParticipants");
    //console.log(maxParticipantsEle.value);

    const minMannerTemperatureEle = document.querySelector("#minMannerTemperature");
    //console.log(minMannerTemperatureEle.value);
    //console.log(myMannerTemperature);

    const matchDescriptionEle = document.querySelector("#matchDescription");


    /*
   * 글자 수 검사
   * */
    // 1. 팀 소개 글자 수 검사
    if(isExceedCharlimit(teamIntroEle.value.length, 300)){
        e.preventDefault();
        //alert("팀 소개는 300자 내로 작성해주세요.");
        Swal.fire({text: '팀 소개는 300자 내로 작성해주세요.', icon: 'warning', confirmButtonText: '확인'});
    }

    // 2. 경기장명 글자 수 검사
    if(isExceedCharlimit(sportsFacilityNameEle.value.length, 100)){
        e.preventDefault();
        //alert("경기장명은 100자 내로 작성해주세요.");
        Swal.fire({text: '경기장명은 100자 내로 작성해주세요.', icon: 'warning', confirmButtonText: '확인'});
    }

    // 3. 경기장 주소 글자 수 검사
    if(isExceedCharlimit(sportsFacilityAddressEle.value.length, 100)){
        e.preventDefault();
        //alert("경기장 주소는 100자 내로 작성해주세요.");
        Swal.fire({text: '경기장 주소는 100자 내로 작성해주세요.', icon: 'warning', confirmButtonText: '확인'});
    }

    // 4. 경기 방식 소개 글자 수 검사
    if(isExceedCharlimit(matchDescriptionEle.value.length, 1000)){
        e.preventDefault();
        //alert("경기 방식 소개 1000자 내로 작성해주세요.");
        Swal.fire({text: '경기 방식 소개 1000자 내로 작성해주세요.', icon: 'warning', confirmButtonText: '확인'});
    }

    if(sportsTypeNameEle.value ===""){
        //alert("종목을 선택하세요.");
        Swal.fire({text: '종목을 선택하세요.', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    }else if(teamNameEle.value === ""){
        //alert("팀 이름을 입력하세요.");
        Swal.fire({text: '팀 이름을 입력하세요.', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    } else if(teamIntroEle.value ===""){
        //alert("팀 소개를 입력하세요");
        Swal.fire({text: '팀 소개를 입력하세요', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    } else if(sportsFacilityNameEle.value ===""){
        //alert("경기장명을 입력하세요");
        Swal.fire({text: '경기장명을 입력하세요', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    } else if(sportsFacilityAddressEle.value ===""){
        //alert("경기장 주소를 입력하세요.");
        Swal.fire({text: '경기장 주소를 입력하세요.', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    } else if(currentParticipantCountEle.value ===""){
        //alert("현재 참가 인원을 입력하세요.");
        Swal.fire({text: '현재 참가 인원을 입력하세요.', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    } else if(maxParticipantsEle.value ===""){
        //alert("총 모집 인원을 입력하세요.");
        Swal.fire({text: '총 모집 인원을 입력하세요.', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    }else if(Number(currentParticipantCountEle.value) >Number(maxParticipantsEle.value)){
        //alert(`현재 참가 인원은 총 모집 인원보다 적어야 합니다.`);
        Swal.fire({text: '현재 참가 인원은 총 모집 인원보다 적어야 합니다.', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
   } else if(minMannerTemperatureEle.value ===""){
        //alert("하한 매너 온도를 입력하세요.");
        Swal.fire({text: '하한 매너 온도를 입력하세요.', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    } else if(minMannerTemperatureEle.value>myMannerTemperature){
        //alert(`하한 매너 온도는 내 매너 온도 ${myMannerTemperature} 이하이어야 합니다.`);
        Swal.fire({text: `하한 매너 온도는 내 매너 온도 ${myMannerTemperature} 이하이어야 합니다.`, icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
   } else if(matchDescriptionEle.value ===""){
        //alert("경기 방식 소개를 입력하세요");
        Swal.fire({text: '경기 방식 소개를 입력하세요', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    } else{
        //alert("글 수정이 완료되었습니다.");
        //Swal.fire({text: '글 수정이 완료되었습니다.', icon: 'success', confirmButtonText: '확인'});
        e.preventDefault(); // 기본 제출 막기
        Swal.fire({
            text: '글 수정이 완료되었습니다.',
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

function setSportsType(sportsTypeName){
    const optionSoccerEle = document.querySelector("#option-soccer");
    const optionFutsal = document.querySelector("#option-futsal");

    if(optionSoccerEle.value === sportsTypeName)
        optionSoccerEle.selected = true;
    else
        optionFutsal.selected = true;
}

async function setReservationFile(originalName, savedName){
    const response2 = await fetch(`/matchup/attachment/file?saved-name=${savedName}`,{
        method: "GET",
        credentials: "include"
    })
    if(!response2.ok)
        throw new Error(`HTTP error! Status:${response2.status}`)
    const data2 = await response2.blob();
    const url = window.URL.createObjectURL(data2);
    const aEle = document.querySelector("#reservationLoadBox > span > a");

    aEle.href = url;
    aEle.download = originalName;


    // 한 번 클릭만 가능하게
    aEle.addEventListener("click", () => {
        setTimeout(() => {
            aEle.removeAttribute("href");
            URL.revokeObjectURL(url);
            aEle.style.pointerEvents = "none";
            aEle.style.opacity = "0.5";
            aEle.textContent += " (다운로드 완료)";
        }, 500);
    }, { once: true });

    // aEle.addEventListener("click",function handleClick (){
    //     setTimeout(()=>{
    //         aEle.removeAttribute("href")
    //         URL.revokeObjectURL(url);
    //         aEle.removeEventListener("click", handleClick); // 이벤트 제거
    //         aEle.style.pointerEvents = "none";              // 클릭 불가 처리 (옵션)
    //         aEle.style.opacity = "0.5";
    //     },500)
    // })
    // const aEle2 = document.createElement("a");
    // aEle2.innerHTML = "삭제하기";
    //
    // reservationBoxEle.appendChild(aEle2);

}


function getAddress(){
    new daum.Postcode({
        oncomplete: function(data) {
            // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

            // 각 주소의 노출 규칙에 따라 주소를 조합한다.
            // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
            var addr = ''; // 주소 변수

            //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
            if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                addr = data.roadAddress;
            } else { // 사용자가 지번 주소를 선택했을 경우(J)
                addr = data.jibunAddress;
            }

            document.querySelector("#sportsFacilityAddress").value = addr;
            // 커서를 상세주소 필드로 이동한다.
            document.querySelector("#sportsFacilityAddress").focus();

        }
    }).open();
}


function setMaxParticipants(currentParticipantCount, maxParticipants){

    const selectMax = document.querySelector("#maxParticipants");
    //alert(maxParticipants);
    for(let i=currentParticipantCount; i<=31;i++){
        const option = document.createElement("option");
        option.value = i;
        option.textContent = i;
        if(i===maxParticipants)
            option.selected = true;
            //alert(i);

        selectMax.appendChild(option);
    }
}

function setMannerTemperature(minMannerTemperature, myMannerTemperature){
    const selectManner = document.querySelector("#minMannerTemperature");

    for(let i=30;i<=40;i+=0.5){
        const option = document.createElement("option");
        option.value = i;
        option.textContent = i;
        selectManner.appendChild(option);
        if(i===minMannerTemperature)
            option.selected = true;
    }

    document.querySelector("#minMannerTemperatureLabel").textContent +=`(내 온도: ${myMannerTemperature})`;


}

function setButton(){
    // const deleteBtn = document.querySelector(".delete-btn");
    // deleteBtn.addEventListener("click",async ()=>{
    //     const response = await fetch(`/matchup/board/delete?boardId=${boardId}`,{
    //         method: "GET",
    //         credentials: "include"
    //     })
    //     if(!response.ok)
    //         throw new Error(`HTTP error! Status:${response.status}`)
    //     alert("삭제 완료");
    //     window.location.href="/matchup";
    // })

    // const toggleBtn = document.querySelector("#toggleBtn");
    // const reservationLoadBox = document.querySelector("#reservationLoadBox");
    // const reservationFileBox = document.querySelector("#reservationFileBox");
    //
    // toggleBtn.addEventListener("click",()=>{
    //     const isDelete = toggleBtn.textContent === "파일 변경";
    //
    //     if(isDelete){
    //         toggleBtn.textContent = "변경 취소";
    //         reservationLoadBox.style.display = "none";
    //         reservationFileBox.style.display = "block";
    //         document.querySelector("#reservationFileBox > input").required = true;
    //     }
    //     else{
    //         toggleBtn.textContent = "파일 변경";
    //         reservationLoadBox.style.display = "block";
    //         reservationFileBox.style.display = "none";
    //         document.querySelector("#reservationFileBox > input").required = false;
    //         document.querySelector("#reservationFileBox > input").value = '';
    //
    //     }
    // })
    const toggleBtn = document.querySelector("#toggleBtn");
    const reservationLoadBox = document.querySelector("#reservationLoadBox");
    const reservationFileBox = document.querySelector("#reservationFileBox");
    const fileInput = document.querySelector("#reservationFileInput");

    toggleBtn.addEventListener("click", () => {
        const isChange = toggleBtn.textContent === "파일 변경";

        if (isChange) {
            toggleBtn.textContent = "변경 취소";
            reservationLoadBox.style.display = "none";
            reservationFileBox.style.display = "block";
            fileInput.required = true;
        } else {
            toggleBtn.textContent = "파일 변경";
            reservationLoadBox.style.display = "block";
            reservationFileBox.style.display = "none";
            fileInput.required = false;
            fileInput.value = ""; // 초기화
        }
    });

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

function setDate(matchDatetime, matchEndtime) {
    const matchDateEle = document.querySelector("#matchDate");

    const date = new Date(matchDatetime);
    const endDate = new Date(matchEndtime);

    // const [hour, minute, second] = matchDuration.split(":");
    // const hourNum = parseInt(hour, 10);
    // const minuteNum = parseInt(minute,10);
    // let extraHour = 0
    // let endMinute = 0;
    // if(date.getMinutes()+minuteNum>=60){
    //     extraHour = 1;
    //     endMinute = (date.getMinutes()+minuteNum)%60;
    // }else{
    //     endMinute = date.getMinutes()+minuteNum;
    // }
    //
    // if(date.getHours()+hourNum+extraHour>=24)
    //     endHour = (date.getHours()+hourNum+extraHour) %24;
    // else
    //     endHour = date.getHours()+hourNum+extraHour;
    //
    // let endTime =  `${endHour}시 ${endMinute}분`

    matchDateEle.value =  `${date.getMonth() + 1}/${date.getDate()} ${date.getHours()}시 ${date.getMinutes()}분 -${endDate.getHours()}시 ${endDate.getMinutes()}분`;


}

function isExceedCharlimit(length, limit){

    if(Number(length)>Number(limit))
        return true;
    else
        return false;
}

