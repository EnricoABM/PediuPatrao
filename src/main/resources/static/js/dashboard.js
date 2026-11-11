(() => {
  'use strict';
  const root = document.querySelector('.pp-dashboard');
  if (!root) return;

  const form = root.querySelector('#dashboard-filtros');
  const periodo = form?.querySelector('#periodo');
  const inicio = form?.querySelector('#inicio');
  const fim = form?.querySelector('#fim');
  if (form && periodo && inicio && fim) {
    const syncDates = () => {
      const custom = periodo.value === '';
      inicio.disabled = !custom;
      fim.disabled = !custom;
      inicio.required = custom;
      fim.required = custom;
    };
    periodo.addEventListener('change', syncDates);
    syncDates();
    form.addEventListener('submit', () => { if (periodo.value === '') periodo.disabled = true; });
  }

  const data = window.pediuPatraoDashboard || {};
  if (typeof Chart === 'undefined') return;
  Chart.defaults.font.family = 'system-ui, -apple-system, Segoe UI, sans-serif';
  Chart.defaults.color = '#69768a';
  const money = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });
  const sales = Array.isArray(data.vendas) ? data.vendas : [];
  const states = Array.isArray(data.status) ? data.status : [];

  const salesCanvas = root.querySelector('#pp-sales-chart');
  if (salesCanvas && sales.length) {
    const points = sales.map(item => ({
      date: String(item.data || ''),
      amount: Number(item.faturamento || 0)
    })).sort((a, b) => a.date.localeCompare(b.date));
    new Chart(salesCanvas, {
      type: 'line',
      data: {
        labels: points.map(p => p.date ? p.date.split('-').reverse().join('/') : ''),
        datasets: [{
          label: 'Faturamento', data: points.map(p => p.amount),
          borderColor: '#df7735', backgroundColor: 'rgba(223,119,53,.10)',
          borderWidth: 2.5, pointRadius: points.length > 35 ? 0 : 3,
          pointHoverRadius: 5, fill: true, tension: .32
        }]
      },
      options: {
        maintainAspectRatio: false,
        interaction: { mode: 'index', intersect: false },
        plugins: { legend: { display: false }, tooltip: { callbacks: {
          label: ctx => money.format(ctx.parsed.y || 0)
        } } },
        scales: {
          x: { grid: { display: false }, ticks: { maxTicksLimit: 8, maxRotation: 0 } },
          y: { beginAtZero: true, border: { display: false }, grid: { color: '#edf0f5' },
            ticks: { callback: v => 'R$ ' + Number(v).toLocaleString('pt-BR') } }
        }
      }
    });
  }

  const stateCanvas = root.querySelector('#pp-status-chart');
  if (stateCanvas && states.length) {
    const pretty = text => String(text).toLowerCase().replaceAll('_', ' ')
      .replace(/(^|\s)\S/g, c => c.toUpperCase());
    const palette = ['#df7735', '#5269b7', '#2fa57a', '#9a6ece', '#e3b24c', '#e0717b', '#718398'];
    const colors = states.map((_, i) => palette[i % palette.length]);
    new Chart(stateCanvas, {
      type: 'doughnut',
      data: { labels: states.map(s => pretty(s.status)), datasets: [{
        data: states.map(s => Number(s.quantidade || 0)), backgroundColor: colors,
        borderColor: '#fff', borderWidth: 3, hoverOffset: 4
      }] },
      options: { maintainAspectRatio: false, cutout: '73%', plugins: {
        legend: { display: false }, tooltip: { callbacks: { label: ctx => `${ctx.label}: ${ctx.parsed} pedidos` } }
      } }
    });
    const legend = root.querySelector('#pp-status-legend');
    if (legend) {
      states.forEach((state, i) => {
        const li = document.createElement('li');
        const label = document.createElement('span');
        label.className = 'pp-legend-label';
        const dot = document.createElement('span');
        dot.className = 'pp-legend-dot';
        dot.style.backgroundColor = colors[i];
        const name = document.createElement('span');
        name.textContent = pretty(state.status);
        const count = document.createElement('strong');
        count.textContent = String(state.quantidade ?? 0);
        label.append(dot, name);
        li.append(label, count);
        legend.append(li);
      });
    }
  }
})();
