document.addEventListener('DOMContentLoaded', () => {
    const form = document.querySelector('form');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const formData = new FormData(form);

        try {
            const response = await fetch(form.action, {
                method: 'POST',
                body: formData
            });

            if (!response.ok) {
                let errorMsg = '팀 수정에 실패했습니다.';
                try {
                    const data = await response.json();
                    if (data?.error) errorMsg = data.error;
                } catch (jsonError) {
                    const text = await response.text();
                    if (text) errorMsg = text;
                }
                //alert(errorMsg);
                Swal.fire({text: errorMsg, icon: 'warning', confirmButtonText: '확인'});

                return;
            }

            //alert('팀이 성공적으로 수정되었습니다!');
            Swal.fire({text: '팀이 성공적으로 수정되었습니다!', icon: 'success', confirmButtonText: '확인'}).then(()=>{
                window.location.href = '/team'; // or your desired redirect
            });

        } catch (err) {
            console.error('❌ 팀 수정 중 오류 발생:', err);
            //alert('알 수 없는 오류가 발생했습니다.');
            Swal.fire({text: '알 수 없는 오류가 발생했습니다.', icon: 'warning', confirmButtonText: '확인'});

        }
    });
});