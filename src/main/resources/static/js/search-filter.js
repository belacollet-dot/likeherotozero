function enableLiveFilter(inputId, itemSelector, containerSelector) {
    const input = document.getElementById(inputId);
    if (!input) {
        return;
    }

    const container = containerSelector ? document.querySelector(containerSelector) : document;
    if (!container) {
        return;
    }

    input.addEventListener('input', function () {
        const query = input.value.trim().toLowerCase();
        container.querySelectorAll(itemSelector).forEach(function (item) {
            item.style.display = item.textContent.toLowerCase().includes(query) ? '' : 'none';
        });
    });
}
