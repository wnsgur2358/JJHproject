function getAddress() {
    new daum.Postcode({
        oncomplete: function(data) {
            var addr = '';
            if (data.userSelectedType === 'R') {
                addr = data.roadAddress;
            } else {
                addr = data.jibunAddress;
            }
            document.querySelector("#eventAddress").value = addr;
            document.querySelector("#eventAddress").focus();
        }
    }).open();
}

function checkHostBeforeSubmit() {
    const hostSpan = document.getElementById('hostNameSpan');
    const text = hostSpan?.innerText.trim();

    console.log("HostSpan 내용:", text); // 디버깅용

    if (!text || text === '미등록') {
        //alert("⚠️ 주최기관이 미등록 상태입니다. MY에서 먼저 등록해주세요.");
        Swal.fire({text: '⚠️ 주최기관이 미등록 상태입니다. MY에서 먼저 등록해주세요.', icon: 'warning', confirmButtonText: '확인'}).then(()=>{
            window.location.href = "/mypage";
        });

        return false; // 폼 전송 중단
    }

    return true;
}

