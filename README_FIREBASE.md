# Firebase Auth Setup (GCE Environment)

1. **Frontend Setup**:
   The Firebase Web Configuration is already inside `firebase-config.js` and loaded automatically via `index.html`/`login.html`/`signup.html`.
2. **Backend Setup**:
   The Java backend uses Google **Application Default Credentials (ADC)** natively provided by the Google Compute Engine VM. Do **not** use `GOOGLE_APPLICATION_CREDENTIALS` or a downloaded service account JSON key. The VM service account already has the Firebase Auth Admin API permissions.
3. **Database Changes**:
   Execute the `TPRS/backend/sql/migrate_firebase.sql` file. This adds `firebase_uid` and `email_verified` fields to both the `student` and `teacher` tables. (Already run in this environment).
4. **Admin Teacher Creation Override**:
   Admins can verify/create teachers (supervisors) bypassing email authentication. These teachers will initially use password `csembstu` and can set their own passwords after fully authenticating.
5. **Flows**:
   - Students register and get an email verification. Only after verifying their email can they proceed.
   - Using the `sendPasswordResetEmail` in Firebase JS API handles forget passwords.
   - Teachers are similarly created, or managed by an admin.
