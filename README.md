# 2026-Autumn-Aberdeen-21-PetHealthManager
Pet Health Manager is an Android-first app that keeps a complete, structured health record for your pets. Vaccination reminders and visit summaries stay on-device by default, with optional family sharing and AI-assisted analysis.

# Pet Health Manager

> A local-first health management app for pet owners.

A lightweight health management tool for first-time urban pet owners. It turns vaccination, deworming, medication and clinic-visit records into one continuous, exportable and shareable health timeline.

## Overview

Pet Health Manager is an Android-first, Chinese-language health management app. Users can create a local pet profile on first launch without registering an account, and sign in later only when they want family sharing or cloud backup. The app integrates no advertising SDK and does not request contacts, location or other unnecessary permissions; health data stays on the device by default.

The project is currently at the business-plan and product-validation stage. No public installable release is available yet.

## Core Features

1. Pet profiles: species, age, weight, neutering status, allergies and medical history, with separate records for multiple pets.
2. Reminder center: vaccination, deworming, medication and follow-up schedules that fire locally even when the app is closed.
3. Visit summaries and export: scattered records are condensed into a one-page visit summary and can be exported in one tap, so data survives a phone change or a clinic switch.
4. Family sharing: invite family members or caregivers to maintain records together, with owner, editor and viewer roles.
5. Optional AI assistant: generates symptom analysis and report summaries from structured records; every request requires explicit user authorization and is de-identified.
6. Local-first privacy: data is stored in a local database by default; cloud sync and encrypted backup are opt-in, and users can export or permanently delete all data at any time.

## Target Users

The primary users are urban pet owners aged 20–35 who are raising a cat or dog for the first time. They manage daily life on their phones, but health information is scattered across WeChat favorites, notes apps, paper vaccination booklets and clinic mini-programs. Vaccination dates are easily forgotten, and owners struggle to describe a complete medical history during visits.

## Technical Approach

1. Client: Kotlin, Jetpack Compose and Material 3, with a single Activity, Compose navigation, dark mode and large-text support.
2. Architecture and data: MVVM with Hilt; Room is the single source of truth for local data, and DataStore holds user preferences and privacy settings.
3. Background tasks: WorkManager schedules vaccination, deworming, medication and follow-up reminders, plus optional encrypted backup and family sync.
4. Optional cloud services: Firebase Authentication and Firestore provide accounts, sharing and backup; AI capabilities are provided through the Gemini API. Both are optional.
5. Quality and operations: Firebase Test Lab for compatibility testing and Crashlytics for crash monitoring.

## Business Model

The product uses a freemium model with subscription as the core revenue stream, supplemented by value-added services, pet insurance referrals, veterinary clinic partnerships and product recommendations. Year 3 targets are 4,500 monthly active users, 450 paying users and ARR of RMB 76,800, at a net margin of about 75%. Full projections are in Chapter 8 of the business plan.

## Status

The complete business plan is available in Chinese and English. The team is preparing four low-cost validation channels: a seed-user beta, campus pet communities, veterinary clinic and pet store pilots, and a closed app-store test. Pass criteria are defined in Section 6.2.1 of the business plan.

## Repository Contents

| File  | Description |
| `宠物健康管家商业计划书.docx` | Chinese business plan, current version, 11 chapters, 37 pages |
| `Pet Health Manager Business Plan.docx` | English business plan, corresponding content, 41 pages |
| `build_plan.py` | Generator for the Chinese business plan |
| `build_plan_en.py` | Generator for the English business plan |
| `charts/`, `charts_en/` | Grayscale chart assets used by both documents |

## Regenerating the Documents

Requires Python 3.10+ with `python-docx`, `matplotlib` and `numpy`:

```bash
pip install python-docx matplotlib numpy
python build_plan.py
python build_plan_en.py
```

The scripts regenerate the charts under `charts/` and `charts_en/` and rebuild both Word documents. The table of contents is a Word field; if Word asks whether to update fields on first open, choose "Yes" to refresh page numbers.

## Data Notes

Market size, consumption structure and user-research figures come from public industry reports and university surveys, with sources listed in the appendix. Team size, pricing, acquisition speed and financial projections are reasonable assumptions and are labeled as such in the text. No survey or interview results were fabricated. These numbers will be calibrated once the planned primary research is completed.

## Team

| Member | Responsibility |
| --- | --- |
| Lu Siyu | Product and technology, team lead |
| He Yuxin | Marketing and user research |
| Huang Lidian | Business and finance |
