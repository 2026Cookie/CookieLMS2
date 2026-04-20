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
            #session-timer-box.warn {
                color: #7a4e10;
            }
            #session-timer-box.danger {
                color: #8a1a10;
                animation: timerPulse 0.8s ease-in-out infinite alternate;
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
            #session-timer-bar {
                position: fixed;
                top: 0;
                right: 0;
                left: 0;
                z-index: 9999;
                background: rgba(245,235,224,0.95);
                border-bottom: 1px solid #d6ccc2;
                padding: 8px 20px;
                display: flex;
                justify-content: flex-end;
                align-items: center;
                box-shadow: 0 1px 4px rgba(44,32,16,0.08);
            }
            #user-greeting {
                font-size: 13px;
                font-weight: 600;
                color: #5c4a3a;
                white-space: nowrap;
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

        const headerRight = document.querySelector('.header-right');
        const header = document.querySelector('header');

        if (headerRight) {
            // .header-right 가 있으면 그 안에 인라인 배치
            headerRight.insertBefore(box, headerRight.firstChild);
        } else if (header) {
            // header는 있지만 .header-right 없음: .header-right 동적 생성
            const div = document.createElement('div');
            div.className = 'header-right';
            div.style.cssText = 'display:flex;align-items:center;gap:16px;';
            div.appendChild(box);
            header.appendChild(div);
        } else {
            // header 없는 카드 레이아웃: 상단 고정 바에 배치
            const bar = document.createElement('div');
            bar.id = 'session-timer-bar';
            bar.appendChild(box);
            document.body.insertBefore(bar, document.body.firstChild);
            document.body.style.paddingTop = (parseFloat(getComputedStyle(document.body).paddingTop) + 44) + 'px';
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

    function injectNicknameGreeting() {
        fetch('/api/me', { credentials: 'same-origin' })
            .then(function (res) { return res.ok ? res.json() : null; })
            .then(function (data) {
                if (!data || !data.nickname) return;
                const greeting = document.createElement('span');
                greeting.id = 'user-greeting';
                const bold = document.createElement('b');
                bold.textContent = data.nickname;
                greeting.appendChild(bold);
                greeting.appendChild(document.createTextNode('님 환영합니다!!'));

                const box = document.getElementById('session-timer-box');
                if (box && box.parentNode) {
                    box.parentNode.insertBefore(greeting, box.nextSibling);
                }
            })
            .catch(function () {});
    }

    function init() {
        injectStyles();
        createTimerBox();
        injectNicknameGreeting();
        intervalId = setInterval(tick, 1000);
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
