---
name: Mr. Robot Cyberpunk System
colors:
  surface: '#fcf8fa'
  surface-dim: '#dcd9db'
  surface-bright: '#fcf8fa'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f6f3f5'
  surface-container: '#f0edef'
  surface-container-high: '#eae7e9'
  surface-container-highest: '#e4e2e4'
  on-surface: '#1b1b1d'
  on-surface-variant: '#45464d'
  inverse-surface: '#303032'
  inverse-on-surface: '#f3f0f2'
  outline: '#76777d'
  outline-variant: '#c6c6cd'
  surface-tint: '#565e74'
  primary: '#000000'
  on-primary: '#ffffff'
  primary-container: '#131b2e'
  on-primary-container: '#7c839b'
  inverse-primary: '#bec6e0'
  secondary: '#0058be'
  on-secondary: '#ffffff'
  secondary-container: '#2170e4'
  on-secondary-container: '#fefcff'
  tertiary: '#000000'
  on-tertiary: '#ffffff'
  tertiary-container: '#271901'
  on-tertiary-container: '#98805d'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dae2fd'
  primary-fixed-dim: '#bec6e0'
  on-primary-fixed: '#131b2e'
  on-primary-fixed-variant: '#3f465c'
  secondary-fixed: '#d8e2ff'
  secondary-fixed-dim: '#adc6ff'
  on-secondary-fixed: '#001a42'
  on-secondary-fixed-variant: '#004395'
  tertiary-fixed: '#fcdeb5'
  tertiary-fixed-dim: '#dec29a'
  on-tertiary-fixed: '#271901'
  on-tertiary-fixed-variant: '#574425'
  background: '#fcf8fa'
  on-background: '#1b1b1d'
  surface-variant: '#e4e2e4'
  subtle-bg: '#F8FAFC'
  accent-teal: '#10B981'
  warning-amber: '#F59E0B'
  border-subtle: '#E2E8F0'
typography:
  display-xl:
    fontFamily: Geist
    fontSize: 60px
    fontWeight: '800'
    lineHeight: '1.0'
  headline-md:
    fontFamily: Geist
    fontSize: 24px
    fontWeight: '600'
    lineHeight: '1.3'
  body-base:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: '1.5'
  code-snippet:
    fontFamily: JetBrains Mono
    fontSize: 14px
    fontWeight: '400'
    lineHeight: '1.6'
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  stack-xs: 4px
  stack-sm: 8px
  stack-md: 16px
  stack-lg: 24px
  stack-xl: 48px
  inline-gutter: 16px
---

Analyze the design system of this codebase with the goal of creating a DESIGN.md file in the project root and giving the user a file for easy copy & pasting.

Reference material:
  Overview : https://stitch.withgoogle.com/docs/design-md/overview/
  Format   : https://stitch.withgoogle.com/docs/design-md/format/
  Spec     : https://github.com/google-labs-code/design.md

Examples from the spec repo:
  https://github.com/google-labs-code/design.md/blob/main/examples/atmospheric-glass/DESIGN.md
  https://github.com/google-labs-code/design.md/blob/main/examples/paws-and-paths/DESIGN.md

Requirements:
- Begin with YAML frontmatter containing all structured design tokens
  (colors, typography, spacing, elevation, motion, radii, shadows, etc.)
- Follow with free-form Markdown that describes the look & feel and
  captures design intent that token values alone cannot convey
- The file must be entirely self-contained — do not reference any
  files, variables, or paths from the codebase
- All token values must use valid YAML design token format

If you have access to a running local server or screenshots of the
product, compare your DESIGN.md against the rendered UI. Revise until
both the YAML tokens and the written description faithfully capture
the product's visual identity.