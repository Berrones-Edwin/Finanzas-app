# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/).

## [Unreleased]

### Added
- Refresh token flow with rotation and revocation support
- `POST /api/v1/auth/refresh` endpoint for token renewal
- `TokenType` enum (ACCESS, REFRESH) to distinguish token types
- `RefreshTokenRequest` DTO with validation
- `RefreshTokenException` for dedicated error handling
- Flyway migration `V3` adding `token_type` column to `tokens` table
- Refresh token expiration config (`refresh.expiration=604800000`, 7 days)
