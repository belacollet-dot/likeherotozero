document.addEventListener('DOMContentLoaded', function () {
    const canvas = document.getElementById('emissionChart');
    if (!canvas || !window.chartData || window.chartData.length === 0) {
        return;
    }

    const palette = ['#0f8b6c', '#ff7a45', '#3478e0', '#a83479', '#7834e0', '#e0c334', '#34c3c8', '#c85034'];

    const series = window.chartData.map(function (country, index) {
        const color = palette[index % palette.length];
        return {
            label: country.name + ' (' + country.isoCode + ')',
            allPoints: country.data.map(function (point) {
                return { x: point.year, y: point.value };
            }),
            borderColor: color,
            backgroundColor: color,
            fill: false,
            tension: 0.15,
            pointRadius: 2
        };
    });

    const allYears = series.flatMap(function (s) {
        return s.allPoints.map(function (p) { return p.x; });
    });
    const maxYear = allYears.length ? Math.max.apply(null, allYears) : null;

    function pointsForRange(points, years) {
        if (years === 'all' || maxYear === null) {
            return points;
        }
        const minYear = maxYear - years + 1;
        return points.filter(function (p) { return p.x >= minYear; });
    }

    function buildDatasets(years) {
        return series.map(function (s) {
            return {
                label: s.label,
                data: pointsForRange(s.allPoints, years),
                borderColor: s.borderColor,
                backgroundColor: s.backgroundColor,
                fill: s.fill,
                tension: s.tension,
                pointRadius: s.pointRadius
            };
        });
    }

    const chart = new Chart(canvas, {
        type: 'line',
        data: { datasets: buildDatasets('all') },
        options: {
            responsive: true,
            interaction: { mode: 'x', intersect: false },
            scales: {
                x: {
                    type: 'linear',
                    title: { display: true, text: 'Year' },
                    ticks: {
                        stepSize: 5,
                        precision: 0,
                        callback: function (value) {
                            return value;
                        }
                    }
                },
                y: {
                    title: { display: true, text: 'CO2 Emissions (kt)' }
                }
            },
            plugins: {
                tooltip: {
                    callbacks: {
                        title: function (items) {
                            return items.length ? 'Year ' + items[0].parsed.x : '';
                        }
                    }
                }
            }
        }
    });

    document.querySelectorAll('.range-btn').forEach(function (btn) {
        btn.addEventListener('click', function () {
            document.querySelectorAll('.range-btn').forEach(function (b) {
                b.classList.remove('active');
            });
            btn.classList.add('active');

            const years = btn.dataset.years === 'all' ? 'all' : parseInt(btn.dataset.years, 10);
            chart.data.datasets = buildDatasets(years);
            chart.update();
        });
    });
});
