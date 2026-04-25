# NoorSchool Backend Operations Notes

## Purpose
This document is a quick production runbook for backend deploy and audio serving.
Use it when deployment succeeds but runtime behavior is broken.

## Required GitHub Secrets (backend repo)
- `SPRING_PROFILES_ACTIVE`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `GOOGLE_CLIENT_ID`
- `GOOGLE_CLIENT_SECRET`
- `JWT_SECRET`
- `AUTH_ALLOWED_ORIGINS`
- `AUTH_COOKIE_DOMAIN`
- `OAUTH2_SUCCESS_REDIRECT_URI`
- `OAUTH2_FAILURE_REDIRECT_URI`
- `AZURE_SPEECH_KEY`
- `AZURE_SPEECH_REGION`
- `AZURE_SPEECH_VOICE_NAME`
- `AZURE_SPEECH_OUTPUT_FORMAT`
- `AUDIO_DIR`
- `AUDIO_URL_PREFIX`

## Recommended production values
- `AUDIO_DIR=/home/ubuntu/noorschool-files/audio/words`
- `AUDIO_URL_PREFIX=/audio/words`

## Frontend build env requirement
In the frontend repo, set and inject:
- `VITE_BACKEND_BASE_URL=https://yunki.store`

`VITE_*` values are resolved at build time, so frontend redeploy is required after env changes.

## Backend deploy workflow requirement
`backend/.github/workflows/deploy.yml` must pass audio envs in all 3 places:
- job `env`
- ssh-action `envs`
- generated `.env.prod` block

If one is missing, runtime value can be empty even when the secret exists.

## Nginx routing requirement for audio
In `/etc/nginx/sites-available/noorschool`, keep audio route before SPA fallback:

```nginx
location /audio/words/ {
    proxy_pass http://127.0.0.1:8080;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
}

location / {
    try_files $uri $uri/ /index.html;
}
```

This prevents `/audio/words/*.mp3` requests from being handled by React SPA fallback.

## One-time server directory setup
```bash
mkdir -p /home/ubuntu/noorschool-files/audio/words
chmod -R 755 /home/ubuntu/noorschool-files
```

## Fast verification commands
After deploy, run:

```bash
curl -I https://yunki.store/audio/words/32_ko_normal.mp3
```

Expected:
- `HTTP/1.1 200`
- `Content-Type: audio/mpeg` (or another audio content type)

And check file existence:

```bash
ls -l /home/ubuntu/noorschool-files/audio/words/32_ko_normal.mp3
```

## Common failure signatures
- `No routes matched location "/audio/words/..."`
  - Nginx is routing audio to SPA fallback.
- `NotSupportedError: Failed to load because no supported source was found`
  - Audio URL returned HTML/404 instead of mp3.
- `ORA-00942: table or view does not exist`
  - Missing table/view in production DB or missing privileges for app user.
