document.addEventListener("DOMContentLoaded", function () {
    // 1. 로그인 리디렉션 처리
    const redirectAfterLogin = getThymeleafValue("redirectUrl"); // 서버 값 반영
    if (redirectAfterLogin && redirectAfterLogin !== "/signup") {
        localStorage.setItem("loginRedirectUrl", decodeURIComponent(redirectAfterLogin));
    } else {
        localStorage.removeItem("loginRedirectUrl");
    }

    // 2. 계정 정지 여부 체크 및 표시
    const urlParams = new URLSearchParams(window.location.search);
    const error = urlParams.get('error');
    const dateParam = urlParams.get('date');
    const suspendEl = document.getElementById('suspendMessage');

    if (error === 'suspended') {
        Swal.fire({
            text: '계정이 정지되었습니다. 복구일을 확인해주세요.',
            icon: 'warning',
            confirmButtonText: '확인'
        });

        if (suspendEl) {
            suspendEl.innerHTML = '';

            const mainLine = document.createElement("div");
            mainLine.textContent = "계정이 정지되었습니다.";
            mainLine.style.color = "red";
            mainLine.style.fontWeight = "bold";
            suspendEl.appendChild(mainLine);

            if (dateParam) {
                try {
                    const restoreDate = new Date(dateParam);
                    const formatted = restoreDate.toLocaleString("ko-KR", {
                        year: 'numeric', month: '2-digit', day: '2-digit',
                        hour: '2-digit', minute: '2-digit', second: '2-digit'
                    });

                    const dateLine = document.createElement("div");
                    dateLine.textContent = `복구일: ${formatted}`;
                    dateLine.style.color = "red";
                    dateLine.style.marginTop = "4px";
                    dateLine.style.fontWeight = "bold";
                    suspendEl.appendChild(dateLine);
                } catch {
                    const fallbackLine = document.createElement("div");
                    fallbackLine.textContent = `복구일: ${dateParam}`;
                    fallbackLine.style.color = "red";
                    fallbackLine.style.fontWeight = "bold";
                    suspendEl.appendChild(fallbackLine);
                }
            }
        }
    }

    // 3. 임시 비밀번호 팝업 자동 실행 여부
    const shouldShowPopup = localStorage.getItem("showPasswordPopup") === "true";
    if (shouldShowPopup) {
        fetch("/auth/check", {
            method: "GET",
            credentials: "include"
        })
            .then(res => res.json())
            .then(user => {
                if (user?.isTemporaryPassword) openChangePasswordPopup();
                localStorage.removeItem("showPasswordPopup");
            })
            .catch(() => {
                localStorage.removeItem("showPasswordPopup");
                closeResetPopup();
                closeChangePopup();
            });
    } else {
        closeResetPopup();
        closeChangePopup();
    }

    // 4. 로그인 요청
    document.getElementById("loginForm").addEventListener("submit", function (e) {
        e.preventDefault();

        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;
        let redirect = localStorage.getItem("loginRedirectUrl") || "/main";

        fetch("/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            credentials: 'include',
            body: JSON.stringify({ email, password })
        })
            .then(res => {
            if (!res.ok) {
                return res.json().then(async data => {
                    if (data.error === "존재하지 않는 사용자입니다." || data.error === "탈퇴한 계정입니다.") {
                        await Swal.fire({
                            text: '존재하지 않는 계정입니다. 회원가입 후 이용해주세요.',
                            icon: 'warning',
                            confirmButtonText: '확인',
                            allowOutsideClick: false
                        }).then(()=>{
                            window.location.href = "/signup";
                        });

                    } else if (data.error === "계정이 정지되었습니다.") {
                        const restoreDate = data.restoreDate || '';
                        window.location.href = `/login?error=suspended&date=${encodeURIComponent(restoreDate)}`;
                    } else {
                        throw new Error(data.error || "로그인 실패");
                    }
                });
            }
            return res.json();
        })
            .then(data => {
                if (data.isTemporaryPassword) {
                    localStorage.setItem("showPasswordPopup", "true");
                } else {
                    localStorage.removeItem("showPasswordPopup");
                }

                Swal.fire({
                    text: '로그인 성공!',
                    icon: 'success',
                    confirmButtonText: '확인'
                }).then(() => window.location.href = redirect);
            })
            .catch(err => {
                Swal.fire({ text: err.message, icon: 'warning', confirmButtonText: '확인' });
            });
    });

    // 5. 비밀번호 재설정 팝업 열기
    document.getElementById("openResetPopup").addEventListener("click", function (e) {
        e.preventDefault();
        openResetPopup();
    });

    // 6. 임시 비밀번호 요청
    document.getElementById("resetForm").addEventListener("submit", function (e) {
        e.preventDefault();
        const email = document.getElementById("resetEmail").value;

        fetch("/auth/reset-password", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email })
        })
            .then(res => res.text())
            .then(msg => {
                const resultEl = document.getElementById("resetResult");
                resultEl.innerText = msg;
                resultEl.style.color = "#28a745";
            })
            .catch(err => {
                Swal.fire({ text: "에러: " + err.message, icon: 'warning', confirmButtonText: '확인' });
            });
    });

    // 7. 새 비밀번호 변경 요청
    document.getElementById("changePasswordForm").addEventListener("submit", function (e) {
        e.preventDefault();
        const newPassword = document.getElementById("newPassword").value;
        const confirmPassword = document.getElementById("confirmPassword").value;

        fetch("/auth/change-password", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            credentials: "include",
            body: JSON.stringify({ newPassword, confirmPassword })
        })
            .then(res => {
                if (!res.ok) {
                    return res.text().then(msg => {
                        const resultEl = document.getElementById("changeResult");
                        resultEl.innerText = msg;
                        resultEl.style.color = "red";
                        throw new Error(msg);
                    });
                }
                return res.text();
            })
            .then(msg => {
                const resultEl = document.getElementById("changeResult");
                resultEl.innerText = msg;
                resultEl.style.color = "#28a745";
                setTimeout(() => {
                    closeChangePopup();
                    window.location.href = "/main";
                }, 1500);
            })
            .catch(err => {
                const resultEl = document.getElementById("changeResult");
                resultEl.innerText = err.message || "비밀번호 변경 실패";
                resultEl.style.color = "red";
            });
    });
});

// 헬퍼 함수들
function openResetPopup() {
    document.getElementById("resetPopup").style.display = "block";
    document.getElementById("popupOverlay").style.display = "block";
}
function closeResetPopup() {
    document.getElementById("resetPopup").style.display = "none";
    document.getElementById("popupOverlay").style.display = "none";
    document.getElementById("resetResult").innerText = "";
}
function openChangePasswordPopup() {
    document.getElementById("changePasswordPopup").style.display = "block";
    document.getElementById("popupOverlay").style.display = "block";
}
function closeChangePopup() {
    document.getElementById("changePasswordPopup").style.display = "none";
    document.getElementById("popupOverlay").style.display = "none";
    document.getElementById("changeResult").innerText = "";
}
function getThymeleafValue(name) {
    // e.g., if you use: <script> const redirectAfterLogin = /*[[${redirectUrl}]]*/ ''; </script>
    return '';
}
