let currentPage = 0;

function loadTeamPage(page) {
    document.getElementById('backToAllBtn').style.display = 'none';
    const region = document.getElementById('region').value;
    const position = document.getElementById('recruiting-position').value;
    const ratingValue = document.getElementById('rating-filter').value;
    const rating = ratingValue === "" ? null : parseFloat(ratingValue);
    const recruitmentStatusValue = document.getElementById('recruitmentStatus').value;
    const recruitmentStatus = recruitmentStatusValue === "" ? null : recruitmentStatusValue;

    // study!! - Using URL object for proper query parameter handling
    const url = new URL(`/team/team/list`, window.location.origin);
    url.searchParams.set("page", page);
    if (region) url.searchParams.set("region", region);
    if (position) url.searchParams.set("recruitingPosition", position);
    if (rating !== null) url.searchParams.set("teamRatingAverage", rating);
    if (recruitmentStatus !== null) url.searchParams.set("recruitmentStatus", recruitmentStatus);
    // study!! - Single fetch call with proper error handling
    fetch(url)
        .then(res => {
            if (!res.ok) {
                throw new Error(`HTTP error! status: ${res.status}`);
            }
            return res.json();
        })
        .then(data => {
            const teams = data.data.items;
            const pageInfo = data.data.pageInfo;
            const container = document.getElementById('team-container');
            const paging = document.getElementById('paging-container');

            container.innerHTML = '';
            paging.innerHTML = '';

            if (!teams || teams.length === 0) {
                container.innerHTML = '<p>등록된 팀이 없습니다.</p>';
                container.style.display = 'block';  // ✅ make sure it's visible even when empty
                return;
            }

            teams.forEach(team => {
                console.log("🔍 Team:", team);
                container.innerHTML += `

     <a href="/team/team/${team.teamId}" style="text-decoration: none; color: inherit;">
    <div class="team-card" style="border: 1px solid #ccc; padding: 10px; margin-bottom: 10px; display: flex; align-items: center;">
      <img src="${team.imageUrl || '/img/default-team.png'}" alt="${team.teamName} 이미지" style="width: 80px; height: 80px; margin-right: 15px; object-fit: contain;">
      <div>
        <h3>${team.teamName}</h3>
        <p>지역: ${team.translatedRegion || team.teamRegion || '지역 정보 없음'}</p>
<p>
  별점: ${team.teamRatingAverage.toFixed(1)}
  <span class="star-rating" style="--rating-width: ${team.teamRatingAverage * 20}%"></span>
</p>
        <p>포지션: ${team.recruitingPositionsKorean ? team.recruitingPositionsKorean.join(', ') : 'N/A'}</p>
        <p>${team.recruitmentStatus ? '모집 중' : '모집 완료'}</p>
        <p>작성자: ${team.createdBy || 'N/A'}</p>
      </div>
    </div>
  </a>
`;

            });
            container.style.display = 'block';  // ✅ add this right after the forEach loop

            paging.innerHTML = '';
            const pagination = document.createElement('div');
            pagination.className = 'pagination';
            paging.appendChild(pagination);

// Previous
            if (!pageInfo.isFirst) {
                pagination.innerHTML += `
        <a href="#" class="page-link" onclick="loadTeamPage(${page - 1}); return false;">이전</a>
    `;
            }

// Page Numbers
            for (let i = 0; i < pageInfo.totalPages; i++) {
                pagination.innerHTML += `
        <a href="#" class="page-link ${i === page ? 'active' : ''}" onclick="loadTeamPage(${i}); return false;">
            ${i + 1}
        </a>
    `;
            }

// Next
            if (!pageInfo.isLast) {
                pagination.innerHTML += `
        <a href="#" class="page-link" onclick="loadTeamPage(${page + 1}); return false;">다음</a>
    `;
            }

            currentPage = page;
        })
        // study!! - Added error handling with user feedback
        .catch(error => {
            console.error('Error loading teams:', error);
            const container = document.getElementById('team-container');
            container.innerHTML = '<p>팀 목록을 불러오는 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.</p>';
        });
}

document.addEventListener('DOMContentLoaded', () => {
    loadTeamPage(0);

    // Optional: filter button reloads page 0
    document.getElementById('filterBtn').addEventListener('click', () => {
        loadTeamPage(0);
    });
});
document.getElementById('myTeamBtn').addEventListener('click', async () => {
    try {
        const res = await fetch('/team/team/my-team-info');
        const result = await res.json();
        const team = result.data;

        const container = document.getElementById('team-container');
        const paging = document.getElementById('paging-container');
        container.innerHTML = '';
        paging.innerHTML = '';

        if (!team) {
            container.innerHTML = '<p>소속된 팀이 없습니다.</p>';
            document.getElementById('backToAllBtn').style.display = 'inline-block';  // ✅ SHOW it
            return;
        }

        container.innerHTML = `
        <a href="/team/team/${team.teamId}" style="text-decoration: none; color: inherit;">
            <div class="team-card" id="team-card-${team.teamId}">
                <img src="${team.imageUrl || '/img/default-team.png'}" alt="${team.teamName} 이미지" style="width: 80px; height: 80px; margin-right: 15px; object-fit: contain;">
                <div>
                    <h3>${team.teamName}</h3>
                    <p>지역: ${team.translatedRegion || team.teamRegion || '지역 정보 없음'}</p>

<p>
  별점: ${team.teamRatingAverage.toFixed(1)}
  <span class="star-rating" style="--rating-width: ${team.teamRatingAverage * 20}%"></span>
</p>

                    <p>포지션: ${team.recruitingPositionsKorean ? team.recruitingPositionsKorean.join(', ') : 'N/A'}</p>
                    <p>${team.recruitmentStatus ? '모집 중' : '모집 완료'}</p>
                    <p>팀장: ${team.createdBy || 'N/A'}</p>
                </div>
            </div>
        </a>
        `;

        // ✅ Show "전체 목록으로 돌아가기" button after successful rendering
        document.getElementById('backToAllBtn').style.display = 'inline-block';
    } catch (err) {
        console.error("❌ 내 팀 정보 로딩 실패:", err);
        //alert("소속된 팀이 없습니다.");
        Swal.fire({text: '소속된 팀이 없습니다.', icon: 'warning', confirmButtonText: '확인'});

        document.getElementById('backToAllBtn').style.display = 'inline-block';
    }


});
document.getElementById('backToAllBtn').addEventListener('click', () => {
    loadTeamPage(0);
    document.getElementById('backToAllBtn').style.display = 'none';
});
