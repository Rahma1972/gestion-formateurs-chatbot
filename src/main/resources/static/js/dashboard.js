document.addEventListener('DOMContentLoaded', () => {
    const statutLabels = JSON.parse(document.getElementById('donnees-dashboard').dataset.statutLabels);
    const statutValues = JSON.parse(document.getElementById('donnees-dashboard').dataset.statutValues);
    const moisLabels = JSON.parse(document.getElementById('donnees-dashboard').dataset.moisLabels);
    const moisValues = JSON.parse(document.getElementById('donnees-dashboard').dataset.moisValues);
    const evalLabels = JSON.parse(document.getElementById('donnees-dashboard').dataset.evalLabels);
    const evalValues = JSON.parse(document.getElementById('donnees-dashboard').dataset.evalValues);

    new Chart(document.getElementById('graphiqueStatut'), {
        type: 'pie',
        data: {
            labels: statutLabels,
            datasets: [{
                data: statutValues,
                backgroundColor: ['#2C6E8E', '#B8862F', '#6B7280', '#A23B3B']
            }]
        }
    });

    new Chart(document.getElementById('graphiqueMois'), {
        type: 'bar',
        data: {
            labels: moisLabels,
            datasets: [{
                label: '',
                data: moisValues,
                backgroundColor: '#0E3D3E'
            }]
        },
        options: { scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } } }
    });

    const canvasEval = document.getElementById('graphiqueEvaluations');
    if (canvasEval) {
        new Chart(canvasEval, {
            type: 'bar',
            data: {
                labels: evalLabels,
                datasets: [{
                    label: '',
                    data: evalValues,
                    backgroundColor: '#B8862F'
                }]
            },
            options: { indexAxis: 'y', scales: { x: { min: 0, max: 5 } } }
        });
    }
});