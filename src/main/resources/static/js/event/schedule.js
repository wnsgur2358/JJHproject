const calendar = document.getElementById("calendar");
const monthYear = document.getElementById("monthYear");
const tooltip = document.getElementById("tooltip");

let selectedCell = null;
let selectedDate = null;
let today = new Date();
today.setHours(0, 0, 0, 0); // 오늘 기준 자정으로 설정
let currentYear = today.getFullYear();
let currentMonth = today.getMonth();

const monthNames = ['JAN','FEB','MAR','APR','MAY','JUN','JUL','AUG','SEP','OCT','NOV','DEC'];
const weekDays = ['SUN','MON','TUE','WED','THU','FRI','SAT'];

const regionColors = {
    "CAPITAL_REGION": "#FFC3C3",
    "JEJU": "#FFC582",
    "YEONGNAM_REGION": "#FFEDA3",
    "HONAM_REGION": "#C7FAB8",
    "CHUNGCHEONG_REGION": "#B5DBFF",
    "GANGWON_REGION": "#E7D0FF"
};

const regionLabels = {
    "CAPITAL_REGION": "수도권",
    "JEJU": "제주권",
    "YEONGNAM_REGION": "영남권",
    "HONAM_REGION": "호남권",
    "CHUNGCHEONG_REGION": "충청권",
    "GANGWON_REGION": "강원권"
};

async function renderCalendar(year, month) {
    calendar.replaceChildren();

    const daysFragment = document.createDocumentFragment();


    const daysInMonth = new Date(year, month + 1, 0).getDate();
    const startDay = new Date(year, month, 1).getDay();

    for (let i = 0; i < startDay; i++) {
        const empty = document.createElement("div");
        empty.classList.add("day-cell");
        calendar.appendChild(empty);
    }

    const response = await fetch(`/api/events?year=${year}&month=${month + 1}`);
    const calendarData = await response.json();

    for (let day = 1; day <= daysInMonth; day++) {
        const cell = document.createElement("div");
        cell.classList.add("day-cell");

        const dateObj = new Date(year, month, day);
        dateObj.setHours(0, 0, 0, 0);
        const localDateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;

        const isPast = dateObj < today;

        if (dateObj.toDateString() === today.toDateString()) {
            cell.classList.add("today");
        }

        const dayNum = document.createElement("div");
        dayNum.classList.add("day-number");
        if (dateObj.getDay() === 0) dayNum.classList.add("sunday");
        dayNum.textContent = day;
        cell.appendChild(dayNum);

        const matched = calendarData.find(d => d.date === localDateStr);
        if (matched && matched.events.length > 0) {
            matched.events.forEach(e => {
                const label = document.createElement("div");
                label.classList.add("event-label");
                const displayTitle = e.eventTitle.length > 20 ? e.eventTitle.slice(0, 20) + "..." : e.eventTitle;
                label.textContent = displayTitle;
                label.title = e.eventTitle;
                label.style.backgroundColor = regionColors[e.region] || "#bbb";

                label.addEventListener("click", () => {
                    window.location.href = `/event/${e.id}`;
                });

                label.addEventListener("mouseover", (event) => {
                    tooltip.innerText =
                        `📌 ${e.eventTitle}\n` +
                        `📅 ${localDateStr}\n` +
                        `🏢 ${e.hostName || '미정'}\n` +
                        `📍 ${e.eventAddress || '미정'}\n` +
                        `📝 ${e.eventMethod || '미정'}\n` +
                        `📞 ${e.eventContact || '없음'}`;
                    tooltip.style.display = 'block';
                });

                label.addEventListener("mousemove", (event) => {
                    tooltip.style.left = event.pageX + 10 + 'px';
                    tooltip.style.top = event.pageY + 10 + 'px';
                });
                label.addEventListener("mouseout", () => {
                    tooltip.style.display = 'none';
                });

                cell.appendChild(label);
            });
        }

        // ✅ 과거 날짜 비활성화
        if (!isPast) {
            cell.addEventListener("click", () => {
                if (userRole !== "HOST") return;
                if (selectedCell) selectedCell.classList.remove("selected");
                cell.classList.add("selected");
                selectedCell = cell;
                selectedDate = localDateStr;
            });
        } else {
            cell.style.opacity = "0.5";
            cell.style.pointerEvents = "none";
        }

        daysFragment.appendChild(cell);
    }

    calendar.appendChild(daysFragment);

    monthYear.textContent = `${monthNames[month]} ${year}`;
}

function changeMonth(offset) {
    calendar.classList.add("fade-out");

    setTimeout(() => {
        currentMonth += offset;
        if (currentMonth < 0) {
            currentMonth = 11;
            currentYear--;
        } else if (currentMonth > 11) {
            currentMonth = 0;
            currentYear++;
        }

        renderCalendar(currentYear, currentMonth);
        calendar.classList.remove("fade-out");
    }, 200);
}

document.addEventListener("DOMContentLoaded", () => {
    renderCalendar(currentYear, currentMonth);
});

function submitSelectedDate() {
    if (!selectedDate) {
        //alert("날짜를 선택해주세요.");
        Swal.fire({text: '⚠날짜를 선택해주세요.', icon: 'warning', confirmButtonText: '확인'})
        return;
    }

    const selected = new Date(selectedDate);
    selected.setHours(0, 0, 0, 0);

    if (selected < today) {
        //alert("📅 과거 날짜에는 대회를 등록할 수 없습니다.");
        Swal.fire({text: '📅 과거 날짜에는 대회를 등록할 수 없습니다.', icon: 'warning', confirmButtonText: '확인'})
        return;
    }

    window.location.href = `/event/new?selectedDate=${selectedDate}`;
}
