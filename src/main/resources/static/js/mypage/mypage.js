const positionLabelMap = {
    "GOALKEEPER": "골키퍼", "CENTER_BACK": "센터백", "LEFT_RIGHT_BACK": "풀백",
    "LEFT_RIGHT_WING_BACK": "윙백", "CENTRAL_DEFENSIVE_MIDFIELDER": "수비형 미드필더",
    "CENTRAL_MIDFIELDER": "중앙 미드필더", "CENTRAL_ATTACKING_MIDFIELDER": "공격형 미드필더",
    "LEFT_RIGHT_WING": "윙", "STRIKER_CENTER_FORWARD": "스트라이커",
    "SECOND_STRIKER": "세컨드 스트라이커", "LEFT_RIGHT_WINGER": "윙어"
};

const timeLabelMap = {
    "WEEKDAY_MORNING": "평일 오전", "WEEKDAY_AFTERNOON": "평일 오후", "WEEKDAY_EVENING": "평일 저녁",
    "WEEKEND_MORNING": "주말 오전", "WEEKEND_AFTERNOON": "주말 오후", "WEEKEND_EVENING": "주말 저녁"
};

const currentPosition = /*[[${myPosition}]]*/ "";
const currentTimeType = /*[[${myTimeType}]]*/ "";

document.addEventListener("DOMContentLoaded", () => {
    fetch("/mypage/enums")
        .then(res => res.json())
        .then(data => {
            const positionSelect = document.getElementById("positionSelect");
            const timeSelect = document.getElementById("timeSelect");

            data.positionNames.forEach(pos => {
                const option = document.createElement("option");
                option.value = pos;
                option.text = positionLabelMap[pos] || pos;
                if (pos === currentPosition) option.selected = true;
                positionSelect.appendChild(option);
            });

            data.timeTypes.forEach(time => {
                const option = document.createElement("option");
                option.value = time;
                option.text = timeLabelMap[time] || time;
                if (time === currentTimeType) option.selected = true;
                timeSelect.appendChild(option);
            });
        });
});

function getCookie(name) {
    const cookies = document.cookie.split(';');
    for (const c of cookies) {
        const [k, v] = c.trim().split('=');
        if (k === name) return v;
    }
    return null;
}

function submitMypage() {
    const token = getCookie("Authorization");
    if (!token) {
        //alert("로그인이 필요합니다.");
        Swal.fire({text: '로그인이 필요합니다.', icon: 'warning', confirmButtonText: '확인'});
        return
    }

    const data = {
        positionName: document.getElementById("positionSelect").value,
        timeType: document.getElementById("timeSelect").value,
        temperature: document.getElementById("temperatureInput").value
    };

    fetch("/mypage/update", {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "Authorization": token.startsWith("Bearer ") ? token : `Bearer ${token.trim()}`
        },
        body: JSON.stringify(data)
    }).then(res => {
        if (res.ok) {
            //alert("수정 완료!");
            Swal.fire({text: '수정 완료!', icon: 'success', confirmButtonText: '확인'}).then(()=>{
                location.reload(); // 수정 후 반영되도록 새로고침
            });
        } else {
            res.text().then(msg => {
                //alert("오류: " + msg);
                Swal.fire({text: "오류: " + msg, icon: 'warning', confirmButtonText: '확인'});
            });
            }
        });
}

function uploadProfile() {
    const file = document.getElementById("profileImageInput").files[0];
    if (!file) {
        //alert("파일을 선택해주세요.");
        Swal.fire({text: '파일을 선택해주세요.', icon: 'warning', confirmButtonText: '확인'});
        return
    }
    const formData = new FormData();
    formData.append("profileImage", file);

    fetch("/mypage/uploadProfile", {
        method: "POST",
        body: formData,
        credentials: "include"
    }).then(res => {
        if (res.ok) location.reload();
        else res.text().then(msg => {
            //alert("오류: " + msg);
            Swal.fire({text: "오류: " + msg, icon: 'warning', confirmButtonText: '확인'});
        });
    });
}

function submitHostName() {
    const name = document.getElementById("hostNameInput").value.trim();
    if (!name) {
        //alert("기관명을 입력해주세요.");
        Swal.fire({text: '기관명을 입력해주세요.', icon: 'warning', confirmButtonText: '확인'});
        return
    }
    fetch("/mypage/hostName", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: new URLSearchParams({ hostName: name }),
        credentials: "include"
    }).then(res => {
        if (res.ok) location.reload();
        else res.text().then(msg => {
            //alert("오류: " + msg);
            Swal.fire({text: "오류: " + msg, icon: 'warning', confirmButtonText: '확인'});
        });
    });
}