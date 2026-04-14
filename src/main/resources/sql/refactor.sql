use coookielms;
ALTER TABLE users
    RENAME COLUMN deletedAt TO deleted_at;