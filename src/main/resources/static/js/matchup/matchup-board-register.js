let myMannerTemperature;

document.addEventListener("DOMContentLoaded",async ()=>{
    getTeam(); // 현재 사용자의 팀 정보를 가져옴
    setCurrentParticipant();
    myMannerTemperature = await getMyMannerTemperature();
    setMannerTemperature();

    const form = document.querySelector("form");
    form.addEventListener("submit", (event)=>{
        submitCheck(event)
        }
    );

})

function submitCheck(e){


    const sportsTypeNameEle = document.querySelector("#sportsTypeName");

    const teamNameEle = document.querySelector("#teamName");

    const teamIntroEle = document.querySelector("#teamIntro");

    const reservationFileEle = document.querySelector("#reservationFile");

    const sportsFacilityNameEle = document.querySelector("#sportsFacilityName");

    //document.querySelector("#teamName").disabled=false;
    const sportsFacilityAddress = document.querySelector("#sportsFacilityAddress");

    const matchDatetimeEle = document.querySelector("#matchDatetime");
    //console.log(matchDatetimeEle.value);

    const matchDurationEle = document.querySelector("#matchDuration");

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
        //alert("경기장명은 100자 내로 작성해주세요.")
        Swal.fire({text: '경기장명은 100자 내로 작성해주세요.', icon: 'warning', confirmButtonText: '확인'});
    }

    // 3. 경기장 주소 글자 수 검사
    if(isExceedCharlimit(sportsFacilityAddress.value.length, 100)){
        e.preventDefault();
        //alert("경기장 주소는 100자 내로 작성해주세요.");
        Swal.fire({text: '경기장 주소는 100자 내로 작성해주세요.', icon: 'warning', confirmButtonText: '확인'});
    }

    // 4. 경기 방식 소개 글자 수 검사
    if(isExceedCharlimit(sportsFacilityAddress.value.length, 1000)){
        e.preventDefault();
        //alert("경기 방식 소개 1000자 내로 작성해주세요.");
        Swal.fire({text: '경기 방식 소개 1000자 내로 작성해주세요.', icon: 'warning', confirmButtonText: '확인'});
    }


    if(sportsTypeNameEle.value ===""){
        //alert("종목을 선택하세요.");
        Swal.fire({text: '종목을 선택하세요.', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    } else if(teamNameEle.value ===""){
        //alert("팀 이름을 입력하세요.");
        Swal.fire({text: '팀 이름을 입력하세요.', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    } else if(teamIntroEle.value ===""){
        //alert("팀 소개를 입력하세요.");
        Swal.fire({text: '팀 소개를 입력하세요.', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    } else if(reservationFileEle.value === ""){
        //alert("경기장 예약 내역 파일을 업로드 해주세요.");
        Swal.fire({text: '경기장 예약 내역 파일을 업로드 해주세요.', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    } else if(sportsFacilityNameEle.value ===""){
        //alert("경기장명을 입력하세요.");
        Swal.fire({text: '경기장명을 입력하세요.', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    } else if(sportsFacilityAddress.value ===""){
        //alert("경기장 주소를 입력하세요.");
        Swal.fire({text: '경기장 주소를 입력하세요.', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    } else if(matchDatetimeEle.value === ""){
        //alert("경기 시작 시간을 입력하세요.");
        Swal.fire({text: '경기 시작 시간을 입력하세요.', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    } else if(new Date(matchDatetimeEle.value)< new Date()){
        //alert(`경기 시작 시간은 현재 시간 이후만 가능합니다. 다시 작성해주세요.`);
        Swal.fire({text: '경기 시작 시간은 현재 시간 이후만 가능합니다. 다시 작성해주세요.', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    } else if(matchDurationEle.value ===""){
        //alert("경기 진행 시간을 입력하세요.");
        Swal.fire({text: '경기 진행 시간을 입력하세요.', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    } else if(currentParticipantCountEle.value ===""){
        //alert("현재 참가 인원을 입력하세요.");
        Swal.fire({text: '현재 참가 인원을 입력하세요.', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    } else if(maxParticipantsEle.value ===""){
        //alert("총 모집 인원을 입력하세요.");
        Swal.fire({text: '총 모집 인원을 입력하세요.', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    } else if(Number(currentParticipantCountEle.value) >=Number(maxParticipantsEle.value)){
        //alert(`현재 참가 인원은 총 모집 인원보다 적어야 합니다.`);
        Swal.fire({text: '현재 참가 인원은 총 모집 인원보다 적어야 합니다.', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    } else if(minMannerTemperatureEle.value ===""){
        //alert("하한 매너 온도를 입력하세요.");
        Swal.fire({text: '하한 매너 온도를 입력하세요.', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    } else if(Number(minMannerTemperatureEle.value) > Number(myMannerTemperature)){
        //alert(`하한 매너 온도는 작성자의 매너온도(${myMannerTemperature})이하로 지정해주세요.`);
        Swal.fire({text: `하한 매너 온도는 작성자의 매너온도(${myMannerTemperature})이하로 지정해주세요.`, icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    } else if(matchDescriptionEle.value ===""){
        //alert("경기 방식 소개를 입력하세요.");
        Swal.fire({text: '경기 방식 소개를 입력하세요.', icon: 'warning', confirmButtonText: '확인'});
        e.preventDefault();
    } else{

       // let reply = confirm("경기 날짜와 진행 시간은 수정이 불가능합니다. 진행하시겠습니까?");
        e.preventDefault();
        Swal.fire({
            text: '경기 날짜와 진행 시간은 수정이 불가능합니다. 진행하시겠습니까?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: '예',
            cancelButtonText: '아니요'
        }).then((result) => {
            if (result.isConfirmed) {
                // 사용자가 '네'를 눌렀을 때 처리
                    //alert("게시글이 등록되었습니다.");
                    //Swal.fire({text: '게시글이 등록되었습니다.', icon: 'success', confirmButtonText: '확인'});
                    // 기본 제출 막기
                    Swal.fire({
                        text: '게시글이 등록되었습니다.',
                        icon: 'success',
                        confirmButtonText: '확인'
                    }).then((result2) => {
                        if (result2.isConfirmed) {
                            // 버튼에서 올라가며 가장 가까운 form 찾기
                            e.target.closest("form").submit();
                        }
                    });
            }
        });

        // if(reply){
        //     //alert("게시글이 등록되었습니다.");
        //     Swal.fire('완료!', '게시글이 등록되었습니다.', 'success');
        // }else{
        //     e.preventDefault();
        // }
    }
}

/*
* 현재 로그인한 사용자가 소속된 팀 이름을 가져옴
* */
async function getTeam(){
    const teamName = document.querySelector("#teamName");
    const teamIntro = document.querySelector("#teamIntro");
    const response = await fetch(`/member/search/team-name`,{
        method: "GET",
        credentials: "include"
    });
    if(!response.ok){

        // let reply = confirm("Matchup 게시글에 글 작성을 하기 위해서는 소속팀이 있어야 합니다. 팀 등록을 하시겠습니까?");
        // if(reply)
        //     window.location.href = "/team/team/register";
        // else
        //     window.history.back();
        Swal.fire({
            title: '팀 등록이 필요합니다',
            text: 'Matchup 게시글에 글 작성을 하기 위해서는 소속팀이 있어야 합니다. 팀 등록을 하시겠습니까?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: '네, 등록할게요',
            cancelButtonText: '아니요'
        }).then((result) => {
            if (result.isConfirmed) {
                // 사용자가 '네'를 눌렀을 때 처리
                window.location.href = '/team/team/register'; // 예시: 팀 등록 페이지로 이동
            } else {
                // 사용자가 '아니요' 눌렀을 때
                window.history.back();
            }
        });

    }

    const data = await response.json();

    teamName.value = data.data.teamName;
    if(data.data.teamIntro!=='')
        teamIntro.value = data.data.teamIntro;
    else
        teamIntro.value = "팀 소개를 입력하세요";

}

function getAddress() {
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

function setCurrentParticipant(){

    const selectCur = document.querySelector("#currentParticipantCount");
    const selectMax = document.querySelector("#maxParticipants");

    for(let i=1; i<=30;i++){
        const option1 = document.createElement("option");
        const option2 = document.createElement("option");
        option1.value = i;
        option1.textContent = i;
        selectCur.appendChild(option1);
        option2.value = i+1;
        option2.textContent = i+1;
        selectMax.appendChild(option2);
    }
}

function setMannerTemperature(){
    const selectManner = document.querySelector("#minMannerTemperature");

    for(let i=30;i<=40;i+=0.5){
        const option = document.createElement("option");
        option.value = i;
        option.textContent = i;
        selectManner.appendChild(option);
    }
}

async function getMyMannerTemperature(){
    try{
        const response  = await fetch(`/member/search/my-temperature`,{
            method: "GET",
            credentials: "include"
        })
        if(!response.ok){
            throw new Error(`HTTP error! Status:${response.status}`)
        }
        const data = await response.json();
        document.querySelector("#temp-tabel").textContent +=`(내 온도: ${data.data})`;
        return data.data;
    }catch(err){
        console.log(err);
        return 20;
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