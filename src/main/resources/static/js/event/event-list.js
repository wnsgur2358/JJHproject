document.addEventListener('DOMContentLoaded', function () {
    const container = document.getElementById('eventTableContainer');

    container.addEventListener('click', function (e) {
        const target = e.target;
        if (target.tagName === 'A' && target.hasAttribute('data-page')) {
            e.preventDefault();
            const page = target.getAttribute('data-page');
            fetch(`/event/my?page=${page}`, {
                headers: { 'X-Requested-With': 'XMLHttpRequest' }
            })
                .then(res => res.text())
                .then(html => {
                    const parser = new DOMParser();
                    const doc = parser.parseFromString(html, 'text/html');
                    const newTable = doc.querySelector('#eventTableContainer');
                    container.innerHTML = newTable.innerHTML;
                });
        }
    });
});