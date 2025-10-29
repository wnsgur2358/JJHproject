document.addEventListener('DOMContentLoaded', () => {
    const joinTeamBtn = document.getElementById('joinTeamBtn');
    const chatBtn = document.getElementById('chatBtn');
    const modal = document.getElementById('joinModal');
    const closeBtn = document.querySelector('.close');
    const joinTeamForm = document.getElementById('joinTeamForm');

    // Get team ID from URL
    const teamId = window.location.pathname.split('/').pop();

    // ✅ Tab Toggle Setup
    const infoSection = document.querySelector('.team-info');
    const reviewSection = document.getElementById('reviewSection');
    const tabInfo = document.getElementById('tab-info');
    const tabReviews = document.getElementById('tab-reviews');

// Toggle tabs
    if (tabInfo && tabReviews && infoSection && reviewSection) {
        tabInfo.addEventListener('click', (e) => {
            e.preventDefault();
            infoSection.style.display = 'block';
            reviewSection.style.display = 'none';
            tabInfo.classList.add('active');
            tabReviews.classList.remove('active');
        });

        tabReviews.addEventListener('click', (e) => {
            e.preventDefault();
            infoSection.style.display = 'none';
            reviewSection.style.display = 'block';
            tabReviews.classList.add('active');
            tabInfo.classList.remove('active');
        });
    }

    // Join Team Button Click
    if (joinTeamBtn) {
        joinTeamBtn.addEventListener('click', () => {
            modal.style.display = 'block';
        });
    }

    // Chat Button Click
    if (chatBtn) {
        chatBtn.addEventListener('click', async () => {
            try {
                const response = await fetch(`/chat/room/${teamId}`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });

                if (!response.ok) {
                    throw new Error('채팅방 생성에 실패했습니다.');
                }

                const data = await response.json();
                if (data.success) {
                    window.location.href = `/chat/room/${data.data.roomId}`;
                } else {
                    //alert('채팅방 생성에 실패했습니다. 다시 시도해주세요.');
                    Swal.fire({text: '채팅방 생성에 실패했습니다. 다시 시도해주세요.', icon: 'warning', confirmButtonText: '확인'});
                }
            } catch (error) {
                console.error('Error:', error);
                //alert('채팅 기능 사용 중 오류가 발생했습니다.');
                Swal.fire({text: '채팅 기능 사용 중 오류가 발생했습니다.', icon: 'warning', confirmButtonText: '확인'});
            }
        });
    }

    // Close Modal
    if (closeBtn) {
        closeBtn.addEventListener('click', () => {
            modal.style.display = 'none';
        });
    }

    // Click outside modal to close
    window.addEventListener('click', (event) => {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });

    // Handle Join Team Form Submit
    if (joinTeamForm) {
        joinTeamForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const position = document.getElementById('position').value;
            const introduction = document.getElementById('introduction').value;

            try {
                const response = await fetch(`/team/join/${teamId}`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        position: position,
                        introduction: introduction
                    })
                });

                if (!response.ok) {
                    throw new Error('팀 가입 신청에 실패했습니다.');
                }

                const data = await response.json();
                if (data.success) {
                    //alert('팀 가입 신청이 완료되었습니다.');
                    Swal.fire({text: '팀 가입 신청이 완료되었습니다.', icon: 'warning', confirmButtonText: '확인'});

                    modal.style.display = 'none';
                    joinTeamForm.reset();
                } else {
                    //alert(data.message || '팀 가입 신청에 실패했습니다. 다시 시도해주세요.');
                    Swal.fire({text: data.message || '팀 가입 신청에 실패했습니다. 다시 시도해주세요.', icon: 'warning', confirmButtonText: '확인'});

                }
            } catch (error) {
                console.error('Error:', error);
                //alert('팀 가입 신청 중 오류가 발생했습니다.');
                Swal.fire({text: '팀 가입 신청 중 오류가 발생했습니다.', icon: 'warning', confirmButtonText: '확인'});

            }
        });
    }
    // ✅ Load Reviews
    fetch(`/team/${teamId}/reviews`)
        .then(res => res.json())
        .then(data => {
            const container = document.getElementById('reviewList');
            if (!container) return;

            if (data.data.length === 0) {
                container.innerHTML = '<p>아직 후기가 없습니다.</p>';
                return;
            }

            container.innerHTML = data.data.map(r => `
            <div class="review-box" style="margin-bottom: 1em; border-bottom: 1px solid #ccc; padding-bottom: 1em;">
                <p><strong>${r.reviewerName || '익명'}</strong> ⭐${r.rating}</p>
                <p>${r.content}</p>
                <small>${r.createdDate.split('T')[0]}</small>
            </div>
        `).join('');
        })
        .catch(err => {
            console.error('후기 로딩 실패:', err);
            document.getElementById('reviewList').innerHTML = '<p>후기를 불러오는 데 문제가 발생했습니다.</p>';
        });

// ✅ Submit Review
    const reviewForm = document.getElementById('reviewForm');
    if (reviewForm) {
        reviewForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const content = document.getElementById('reviewContent').value;
            const rating = +document.querySelector('input[name="rating"]:checked')?.value;

            if (!rating || content.trim() === '') {
                //alert('별점과 내용을 모두 입력해주세요.');
                Swal.fire({text: '별점과 내용을 모두 입력해주세요.', icon: 'warning', confirmButtonText: '확인'});
                return;
            }

            try {
                const response = await fetch(`/team/${teamId}/review`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ rating, content })
                });

                if (!response.ok) throw new Error();

                //alert('후기가 등록되었습니다!');
                Swal.fire({text: '후기가 등록되었습니다!', icon: 'warning', confirmButtonText: '확인'}).then(()=>{
                    location.reload();
                });

            } catch (err) {
                console.error(err);
                //alert('후기 등록에 실패했습니다.');
                Swal.fire({text: '후기 등록에 실패했습니다..', icon: 'warning', confirmButtonText: '확인'});

            }
        });
    }
}); 