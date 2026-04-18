(function () {
    const SESSION_MINUTES = 15;
    const WARN_SECONDS = 120;   // 2분 이하: 경고색
    const DANGER_SECONDS = 30;  // 30초 이하: 위험색

    let remaining = SESSION_MINUTES * 60;
    let intervalId;

    function pad(n) {
        return String(n).padStart(2, '0');
    }

    function formatTime(secs) {
        const m = Math.floor(secs / 60);
        const s = secs % 60;
        return pad(m) + ':' + pad(s);
    }

    function injectStyles() {
        const style = document.createElement('style');
        style.textContent = `
            #session-timer-box {
                display: flex;
                align-items: center;
                gap: 8px;
                font-family: 'Segoe UI', system-ui, sans-serif;
                font-size: 13px;
                color: #5c4a3a;
                user-select: none;
                white-space: nowrap;
            }
            #session-timer-box.inline {
                /* 헤더 안에 인라인 배치 - 추가 스타일 불필요 */
            }
            #session-timer-box.floating {
                position: fixed;
                top: 14px;
                right: 20px;
                z-index: 9999;
                background: rgba(245,235,224,0.95);
                border: 1.5px solid #d6ccc2;
                border-radius: 8px;
                padding: 6px 12px;
                box-shadow: 0 2px 10px rgba(44,32,16,0.12);
            }
            #session-timer-box.warn {
                color: #7a4e10;
            }
            #session-timer-box.floating.warn {
                border-color: #c8823a;
                background: rgba(252,243,228,0.97);
            }
            #session-timer-box.danger {
                color: #8a1a10;
                animation: timerPulse 0.8s ease-in-out infinite alternate;
            }
            #session-timer-box.floating.danger {
                border-color: #b03020;
                background: rgba(252,235,228,0.97);
            }
            @keyframes timerPulse {
                from { opacity: 1; }
                to   { opacity: 0.6; }
            }
            #session-timer-label {
                font-size: 11px;
                font-weight: 600;
                letter-spacing: 0.3px;
                opacity: 0.75;
            }
            #session-timer-value {
                font-weight: 700;
                font-size: 14px;
                min-width: 40px;
                text-align: center;
            }
            #session-timer-extend {
                padding: 3px 9px;
                background: transparent;
                border: 1px solid currentColor;
                border-radius: 5px;
                font-size: 12px;
                font-weight: 600;
                cursor: pointer;
                color: inherit;
                transition: background 0.15s;
            }
            #session-timer-extend:hover {
                background: rgba(0,0,0,0.07);
            }
        `;
        document.head.appendChild(style);
    }

    function createTimerBox() {
        const box = document.createElement('div');
        box.id = 'session-timer-box';

        const label = document.createElement('span');
        label.id = 'session-timer-label';
        label.textContent = '세션';

        const value = document.createElement('span');
        value.id = 'session-timer-value';
        value.textContent = formatTime(remaining);

        const btn = document.createElement('button');
        btn.id = 'session-timer-extend';
        btn.textContent = '연장';
        btn.addEventListener('click', extendSession);

        box.appendChild(label);
        box.appendChild(value);
        box.appendChild(btn);

        // .header-right 가 있으면 그 안에 자연스럽게 삽입, 없으면 fixed 오버레이
        const headerRight = document.querySelector('.header-right');
        if (headerRight) {
            box.classList.add('inline');
            headerRight.insertBefore(box, headerRight.firstChild);
        } else {
            box.classList.add('floating');
            document.body.appendChild(box);
        }
    }

    function updateDisplay() {
        const box = document.getElementById('session-timer-box');
        const value = document.getElementById('session-timer-value');
        if (!box || !value) return;

        value.textContent = formatTime(remaining);
        box.classList.remove('warn', 'danger');
        if (remaining <= DANGER_SECONDS) {
            box.classList.add('danger');
        } else if (remaining <= WARN_SECONDS) {
            box.classList.add('warn');
        }
    }

    function tick() {
        remaining--;
        if (remaining <= 0) {
            remaining = 0;
            updateDisplay();
            clearInterval(intervalId);
            // 세션 만료 -> 로그인 페이지로 이동
            window.location.href = '/user/login?expired=true';
        } else {
            updateDisplay();
        }
    }

    function extendSession() {
        fetch('/api/session/keep-alive', { method: 'GET', credentials: 'same-origin' })
            .then(function (res) {
                if (res.ok) {
                    remaining = SESSION_MINUTES * 60;
                    updateDisplay();
                }
            })
            .catch(function () {
                // 네트워크 오류 시 무시
            });
    }

    function init() {
        injectStyles();
        createTimerBox();
        intervalId = setInterval(tick, 1000);
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
