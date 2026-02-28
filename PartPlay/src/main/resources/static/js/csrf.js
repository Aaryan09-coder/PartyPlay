/**
 * csrf.js — Production-safe CSRF token helper
 *
 * Spring writes an XSRF-TOKEN cookie after the user's first request.
 * Every state-changing fetch call (POST, PUT, DELETE) must read that
 * cookie and send it back as the X-XSRF-TOKEN header.
 *
 * Usage — replace every bare fetch() with csrfFetch():
 *   const res = await csrfFetch('/rooms/create', { method: 'POST' });
 *
 * GET requests do not need CSRF protection and can still use plain fetch().
 */

function getCsrfToken() {
    const match = document.cookie
        .split('; ')
        .find(row => row.startsWith('XSRF-TOKEN='));
    return match ? decodeURIComponent(match.split('=')[1]) : null;
}

async function csrfFetch(url, options = {}) {
    const method = (options.method || 'GET').toUpperCase();

    // GET and HEAD are safe methods — no CSRF token needed
    if (method === 'GET' || method === 'HEAD') {
        return fetch(url, { credentials: 'include', ...options });
    }

    const token = getCsrfToken();

    const headers = {
        ...(options.headers || {}),
        'X-Requested-With': 'XMLHttpRequest',
    };

    // Only attach the token if it exists (it won't exist on the very first
    // page load before Spring has had a chance to set the cookie)
    if (token) {
        headers['X-XSRF-TOKEN'] = token;
    }

    return fetch(url, {
        ...options,
        credentials: 'include',   // always send the session cookie
        headers,
    });
}