// 날짜 및 입력창 엔터 처리
window.onload = () => {
    const today = new Date();
    const dateString = today.toLocaleDateString("ko-KR", {
        year: 'numeric', month: 'long', day: 'numeric', weekday: 'long'
    });
    const dateEl = document.getElementById("date");
    if (dateEl) dateEl.innerText = dateString;

    const input = document.getElementById("chat-input");
    if (input) {
        input.addEventListener("keypress", function (e) {
            if (e.key === "Enter") sendMessage();
        });
    }

    // 위치 권한 미리 요청
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            () => { console.log("위치 권한 허용됨"); },
            () => { console.warn("위치 권한 거부됨"); }
        );
    }
};

// 챗봇 런처 및 모달 처리
document.addEventListener("DOMContentLoaded", () => {
    const launcher = parent.document.getElementById("chatbot-launcher");
    const modal = parent.document.getElementById("chatbot-modal");
    const closeBtn = parent.document.getElementById("chatbot-close");

    fetch("/auth/check", { credentials: "include" })
        .then(res => {
            if (!res.ok) {
                if (launcher) launcher.remove();
                if (modal) modal.remove();
            }
        });

    document.addEventListener("click", function (e) {
        if (e.target.id === "chatbot-launcher") {
            fetch("/auth/check", { credentials: "include" })
                .then(res => {
                    if (res.ok && modal) {
                        modal.style.display = "flex";
                    } else {
                        //alert("로그인 후 사용 가능합니다.");
                        Swal.fire({text: '로그인 후 사용 가능합니다.', icon: 'warning', confirmButtonText: '확인'});
                    }
                });
        }
    });

    if (closeBtn) {
        closeBtn.addEventListener("click", () => {
            if (modal) modal.style.display = "none";
        });
    }
});

function initChat() {
    const start = document.getElementById("chat-start");
    const ui = document.getElementById("chat-ui");
    if (start) start.style.display = "none";
    if (ui) ui.style.display = "block";
    appendBotMessage("안녕하세요! MatchON 챗봇입니다! 궁금하신 내용을 선택해 주세요!");
}

function appendMessage(text, isUser = false, bold = false) {
    const chatBox = document.getElementById("chat-box");
    if (!chatBox) return;

    const wrapper = document.createElement("div");
    const content = document.createElement("div");
    const time = document.createElement("div");

    wrapper.className = `message ${isUser ? 'user' : 'bot'}`;
    content.className = "msg-content";
    time.className = "timestamp";
    time.textContent = new Date().toTimeString().substring(0, 5);

    const formattedText = isUser
        ? (bold ? `<b>${escapeHtml(text)}</b>` : escapeHtml(text))
        : text;

    content.innerHTML = formattedText;

    if (!isUser) {
        const botIcon = document.createElement("img");
        botIcon.src = "/img/aichatbot.png";
        botIcon.className = "bot-icon";
        wrapper.appendChild(botIcon);
    }

    wrapper.appendChild(content);
    wrapper.appendChild(time);

    setTimeout(() => {
        chatBox.appendChild(wrapper);
        chatBox.scrollTop = chatBox.scrollHeight;
    }, isUser ? 0 : 100);
}

function escapeHtml(str) {
    return str.replace(/[&<>"']/g, match => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;',
    }[match]));
}

function appendBotMessage(text) {
    appendMessage(text, false);
}
function appendUserMessage(text, bold = false) {
    appendMessage(text, true, bold);
}

function sendMessage() {
    const input = document.getElementById("chat-input");
    const message = input.value.trim();
    if (!message) return;
    appendUserMessage(message);
    input.value = "";
    fetchBotReply(message);
}

function sendChip(text) {
    appendUserMessage(text, true);
    fetchBotReply(text);
}

function fetchBotReply(message) {
    appendBotMessage("입력 중...");

    fetch("/api/aichat", {
        method: "POST",
        headers: { 'Content-Type': 'application/json' },
        credentials: "include",
        body: JSON.stringify({ message })
    })
        .then(res => res.json())
        .then(data => {
            const reply = data.reply;
            const chatBox = document.getElementById("chat-box");
            const messages = chatBox.querySelectorAll(".bot .msg-content");

            if (messages.length > 0) {
                messages[messages.length - 1].innerHTML = reply;
            }

            const isFallback = reply.includes("죄송") || reply.includes("이해하지 못했어요");

            const regionMatch = message.match(/(.+?) ?근처/);
            if (regionMatch && regionMatch[1]) {
                const region = regionMatch[1];
                fetch(`/api/stadiums/search?keyword=${region}`)
                    .then(res => res.json())
                    .then(list => {
                        if (isFallback) {
                            const fallbackMsg = chatBox.querySelector(".bot .msg-content");
                            if (fallbackMsg && fallbackMsg.innerText.includes("죄송")) {
                                fallbackMsg.closest(".message").remove();
                            }
                        }

                        const msg = list.length > 0
                            ? list.map(s =>
                                `📍 <b>${s.stadiumName}</b><br>🏟️ ${s.stadiumAddress}<br>📞 ${s.stadiumTel || '정보 없음'}<br><hr>`
                            ).join("")
                            : `${region} 근처에 경기장이 없습니다.`;
                        appendBotMessage(msg);
                    });
                return; // 매칭된 경우 중복 처리 방지
            }

            if (message.includes("내 주변 경기장")) {
                navigator.permissions?.query({ name: 'geolocation' }).then(function(result) {
                    if (result.state === 'denied') {
                        appendBotMessage("⚠️ 위치 권한이 차단되어 있어요! 주소창 왼쪽 🔒을 눌러 '위치 권한 허용'으로 변경해 주세요.");
                        return;
                    }

                    navigator.geolocation.getCurrentPosition(function(position) {
                        const lat = position.coords.latitude;
                        const lng = position.coords.longitude;

                        fetch(`/api/stadiums/nearby?lat=${lat}&lng=${lng}`)
                            .then(res => res.json())
                            .then(list => {
                                if (isFallback) {
                                    const fallbackMsg = chatBox.querySelector(".bot .msg-content");
                                    if (fallbackMsg && fallbackMsg.innerText.includes("죄송")) {
                                        fallbackMsg.closest(".message").remove();
                                    }
                                }

                                const msg = list.length > 0
                                    ? list.map(s =>
                                        `📍 <b>${s.stadiumName}</b><br>🏟️ ${s.stadiumAddress}<br>📞 ${s.stadiumTel || '없음'}<br><hr>`
                                    ).join("")
                                    : "❌ 근처에 경기장이 없습니다.";
                                appendBotMessage(msg);
                            });
                    }, function () {
                        appendBotMessage("⚠️ 위치 정보를 가져올 수 없습니다. 위치 권한을 허용해 주세요.");
                    });
                });
                return; // 중복 처리 방지
            }

            if (message.includes("종료")) {
                const modal = parent.document.getElementById("chatbot-modal");
                setTimeout(() => {
                    if (modal) modal.style.display = "none";
                }, 1500);
            }
        });
}
