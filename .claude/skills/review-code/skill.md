---
name: review-code
description: Reviews code for bugs, security issues, performance, and best practices. Use when asked to review a PR, file, or code snippet.
---

When reviewing code, follow this structured approach:

## 1. Summary

Start with a brief 1-2 sentence summary of what the code does and your overall assessment (e.g., "Looks good with minor suggestions" or "Has issues that should be addressed").

## 2. Review Checklist

Evaluate the code against these categories. Use severity indicators:

- **[CRITICAL]** - Must fix before merging (bugs, security vulnerabilities)
- **[WARNING]** - Should fix, potential issues (performance, edge cases)
- **[SUGGESTION]** - Nice to have (readability, style improvements)
- **[PRAISE]** - Highlight good patterns worth noting

### Categories to Check

| Category | What to Look For |
|----------|------------------|
| **Correctness** | Logic errors, off-by-one, null handling, edge cases |
| **Security** | Injection, XSS, auth issues, secrets exposure, OWASP Top 10 |
| **Performance** | N+1 queries, unnecessary allocations, blocking calls, missing indexes |
| **Error Handling** | Unhandled exceptions, silent failures, missing validation |
| **Maintainability** | Complexity, naming, single responsibility, magic numbers |
| **Testing** | Missing tests, edge cases not covered, test quality |
| **Concurrency** | Race conditions, deadlocks, thread safety |

## 3. Findings

For each finding, provide:

```
### [SEVERITY] Brief title

**Location:** `file.kt:42`

**Issue:** Describe the problem clearly

**Example:** Show the problematic code

**Fix:** Show how to fix it (or suggest an approach)
```

## 4. Architecture Notes

If reviewing larger changes, comment on:
- Does it follow existing patterns in the codebase?
- Are there any DDD/layering violations?
- Is the change in the right place?

## 5. Questions

List any clarifying questions about intent or requirements.

---

## Style Guidelines

- Be constructive, not condescending
- Explain *why* something is an issue, not just *what*
- Acknowledge good code, not just problems
- Prioritize findings by severity
- Offer concrete fixes when possible
- Consider the context (is this a prototype or production code?)
