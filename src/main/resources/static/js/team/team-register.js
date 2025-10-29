document.addEventListener('DOMContentLoaded', () => {
    const form = document.querySelector('form');
    const submitBtn = form.querySelector('button[type="submit"]');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const formData = new FormData(form);

        try {
            const response = await fetch(form.action, {
                method: 'POST',
                body: formData
            });

            if (!response.ok) {
                // Try to parse error message from backend
                let errorMsg = '팀 생성에 실패했습니다.';
                try {
                    const data = await response.json();
                    if (data && data.error) {
                        errorMsg = data.error;
                    }
                } catch (err) {
                    // Not JSON, fallback to text
                    const text = await response.text();
                    if (text) errorMsg = text;
                }
                //alert(errorMsg);
                Swal.fire({text: errorMsg, icon: 'warning', confirmButtonText: '확인'});
                return;
            }

            // Success: redirect or show message
            //alert('팀이 성공적으로 생성되었습니다!');
            Swal.fire({text: '팀이 성공적으로 생성되었습니다!', icon: 'success', confirmButtonText: '확인'}).then(()=>{
                window.location.href = '/team'; // or wherever you want to redirect
            });

        } catch (error) {
            //alert('알 수 없는 오류가 발생했습니다.');
            Swal.fire({text: '알 수 없는 오류가 발생했습니다.', icon: 'warning', confirmButtonText: '확인'});

            console.error(error);
        }
    });
});