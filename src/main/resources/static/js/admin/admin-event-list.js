document.addEventListener("DOMContentLoaded", () => {
    loadEventPage(0);
});

function loadEventPage(page) {
    fetch(`/admin/event/page?page=${page}`)
        .then(res => res.json())
        .then(data => {
            const tableContainer = document.getElementById("eventTable");
            const paginationContainer = document.getElementById("pagination");

            if (data.content.length === 0) {
                tableContainer.innerHTML = '<div class="no-data">등록된 대회가 없습니다.</div>';
                paginationContainer.innerHTML = '';
                return;
            }

            let tableHtml = `
                <table>
                    <thead>
                        <tr>
                            <th>No.</th>
                            <th>대회명</th>
                            <th>등록자</th>
                            <th>등록일</th>
                            <th>상태</th>
                        </tr>
                    </thead>
                    <tbody>
                `;

            data.content.forEach((event, index) => {
                const statusClass = event.eventStatus === 'APPROVED'
                    ? 'status-approved'
                    : (event.eventStatus === 'DENIED' ? 'status-denied' : 'status-pending');

                tableHtml += `
                        <tr>
                            <td>${index + 1 + (data.number * data.size)}</td>
                            <td class="title-cell">
                                <a href="/admin/event/${event.id}" class="detail-link event-title-ellipsis">
                                    ${event.eventTitle}
                                </a>
                            </td>
                            <td>${event.memberName}</td>
                            <td>${event.createdDate.replace("T", " ")}</td>
                            <td><span class="${statusClass}">${event.statusLabel}</span></td>
                        </tr>`;
            });

            tableHtml += '</tbody></table>';
            tableContainer.innerHTML = tableHtml;

            // pagination
            let paginationHtml = '';
            paginationHtml += `<a onclick="loadEventPage(${data.number - 1})" class="${data.first ? 'disabled' : ''}">&laquo;</a>`;

            for (let i = 0; i < data.totalPages; i++) {
                const active = i === data.number ? 'active' : '';
                paginationHtml += `<a onclick="loadEventPage(${i})" class="${active}">${i + 1}</a>`;
            }

            paginationHtml += `<a onclick="loadEventPage(${data.number + 1})" class="${data.last ? 'disabled' : ''}">&raquo;</a>`;

            paginationContainer.innerHTML = `<div class="pagination">${paginationHtml}</div>`;
        });
}