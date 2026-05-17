---
name: Hacker Mode
colors:
  surface: '#131313'
  surface-dim: '#131313'
  surface-bright: '#3a3939'
  surface-container-lowest: '#0e0e0e'
  surface-container-low: '#1c1b1b'
  surface-container: '#201f1f'
  surface-container-high: '#2a2a2a'
  surface-container-highest: '#353534'
  on-surface: '#e5e2e1'
  on-surface-variant: '#e5bdbe'
  inverse-surface: '#e5e2e1'
  inverse-on-surface: '#313030'
  outline: '#ac8889'
  outline-variant: '#5c3f40'
  surface-tint: '#ffb3b6'
  primary: '#ffb3b6'
  on-primary: '#68001a'
  primary-container: '#e11d48'
  on-primary-container: '#fffaf9'
  inverse-primary: '#be0037'
  secondary: '#ffb4ac'
  on-secondary: '#690007'
  secondary-container: '#921517'
  on-secondary-container: '#ff9f95'
  tertiary: '#ffb95f'
  on-tertiary: '#472a00'
  tertiary-container: '#a36700'
  on-tertiary-container: '#fffaf9'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#ffdada'
  primary-fixed-dim: '#ffb3b6'
  on-primary-fixed: '#40000c'
  on-primary-fixed-variant: '#920028'
  secondary-fixed: '#ffdad6'
  secondary-fixed-dim: '#ffb4ac'
  on-secondary-fixed: '#410002'
  on-secondary-fixed-variant: '#8e1214'
  tertiary-fixed: '#ffddb8'
  tertiary-fixed-dim: '#ffb95f'
  on-tertiary-fixed: '#2a1700'
  on-tertiary-fixed-variant: '#653e00'
  background: '#131313'
  on-background: '#e5e2e1'
  surface-variant: '#353534'
typography:
  headline-xl:
    fontFamily: Geist
    fontSize: 48px
    fontWeight: '800'
    lineHeight: '1.1'
    letterSpacing: -0.05em
  headline-md:
    fontFamily: Geist
    fontSize: 24px
    fontWeight: '700'
    lineHeight: '1.2'
    letterSpacing: -0.02em
  body-lg:
    fontFamily: Geist
    fontSize: 16px
    fontWeight: '400'
    lineHeight: '1.6'
    letterSpacing: 0em
  code-sm:
    fontFamily: jetbrainsMono
    fontSize: 13px
    fontWeight: '400'
    lineHeight: '1.5'
    letterSpacing: 0.02em
  label-caps:
    fontFamily: jetbrainsMono
    fontSize: 11px
    fontWeight: '700'
    lineHeight: '1'
    letterSpacing: 0.15em
spacing:
  unit: 4px
  gutter: 16px
  margin: 24px
  container-max: 1440px
---

## Brand & Style

The design system is an aggressive, high-stakes interface designed to evoke the feeling of "elevated access" and systemic intrusion. The aesthetic prioritizes technical density over whitespace, utilizing a mix of **Cyberpunk Minimalism** and **Brutalism** to create an environment that feels both premium and volatile. 

The target audience is tech-literate users who value precision, speed, and a high-fidelity "terminal" experience. The UI should feel like a sophisticated exploit tool—fast, responsive, and visually urgent. Key visual markers include razor-sharp geometry, digital noise, and "liquid glass" surfaces that react to user interaction with a sense of data-driven fluidity.

## Colors

The palette is anchored in a void-like **Deep Charcoal (#050505)** to maximize the luminance of the accent colors. 

- **Primary (Blood Red):** Used for critical actions, active states, and primary data readouts. It should pulsate or glow in high-priority areas.
- **Secondary (Crimson):** Used for backgrounds of interactive elements and lower-priority emphasis.
- **Tertiary (Amber/Orange):** Reserved strictly for warnings, system logs, or "unsafe" terminal outputs.
- **Neutral:** A range of deep greys that provide structure without breaking the high-contrast aesthetic.

All glass surfaces must utilize a dark red tint (`rgba(153, 27, 27, 0.15)`) to maintain the "Hacker Mode" atmosphere even in translucent states.

## Typography

This design system utilizes **Geist** for its clean, geometric precision in headings and body text, ensuring high legibility despite the aggressive styling. **JetBrains Mono** (as a high-performance alternative to Geist Mono) is used for all metadata, labels, and system status updates to reinforce the terminal aesthetic.

Headlines should be tightly tracked and bold, mimicking high-impact digital signage. Small labels must always be uppercase with generous letter spacing to act as "data tags" throughout the interface.

## Layout & Spacing

The layout follows a **Rigid 12-Column Grid** system that emphasizes structural integrity. Content is organized into modular "blades" or "panels."

- **Rhythm:** A 4px base unit ensures all elements align to a technical grid.
- **Density:** Padding is minimized to allow for a high information-to-screen ratio, characteristic of professional monitoring tools.
- **Margins:** Screens maintain a 24px outer margin, but internal components often touch or share "border-walls" to create a monolithic, integrated appearance.

## Elevation & Depth

Depth in this design system is achieved through **Luminance and Glassmorphism** rather than traditional shadows.

1.  **Level 0 (Floor):** Pure `#050505` background.
2.  **Level 1 (Panels):** Surfaces with a subtle `1px` solid border of `#1A1A1A`.
3.  **Level 2 (Glass):** Backdrop blurs (20px) with a dark red tint and a `1px` semi-transparent crimson border.
4.  **Level 3 (Elevated):** Elements gain a "Glow-Border"—an outer box-shadow with 0 blur and a 2px spread of the primary blood red, creating a "hot" edge effect.

Liquid glass effects are applied to active states, creating a subtle refractive distortion behind primary buttons.

## Shapes

The design system employs **Sharp (0px) geometry** for all structural elements. Every container, button, and input field must have square corners to maintain a brutalist, technical feel. 

Partial 45-degree "clipped" corners are permitted for specialized status badges or primary action buttons to evoke military or aerospace HUD (Heads-Up Display) aesthetics.

## Components

### Buttons
Primary buttons are solid Blood Red (#E11D48) with black monospaced text. They feature a persistent 2px outer glow. Ghost buttons use a 1px Crimson border and shift to a tinted glass fill on hover.

### Input Fields
Inputs are dark glass panels with a bottom-only border. When focused, the bottom border expands to 2px and the entire field gains a subtle red inner-glow. Placeholder text should look like command-line prompts (e.g., `_root > `).

### Cards / Modules
Cards do not use shadows. They are defined by 1px solid borders and "header bars"—a thin strip of Crimson at the top of the card containing the title in uppercase mono-type.

### Data Chips
Small, rectangular tags with a background of `#991B1B` at 20% opacity. They display system statuses or categories.

### Terminal Output (Special Component)
A scrolling text area for logs using the Tertiary (Amber) color for low-level system events and Blood Red for critical errors. This component should feature a subtle scan-line overlay.