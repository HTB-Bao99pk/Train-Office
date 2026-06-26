---
name: RailJet Velocity
colors:
  surface: '#f8f9ff'
  surface-dim: '#cbdbf5'
  surface-bright: '#f8f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#eff4ff'
  surface-container: '#e5eeff'
  surface-container-high: '#dce9ff'
  surface-container-highest: '#d3e4fe'
  on-surface: '#0b1c30'
  on-surface-variant: '#44474c'
  inverse-surface: '#213145'
  inverse-on-surface: '#eaf1ff'
  outline: '#74777d'
  outline-variant: '#c4c6cd'
  surface-tint: '#4f6073'
  primary: '#041627'
  on-primary: '#ffffff'
  primary-container: '#1a2b3c'
  on-primary-container: '#8192a7'
  inverse-primary: '#b7c8de'
  secondary: '#00658d'
  on-secondary: '#ffffff'
  secondary-container: '#3dbeff'
  on-secondary-container: '#004a69'
  tertiary: '#121617'
  on-tertiary: '#ffffff'
  tertiary-container: '#262a2c'
  on-tertiary-container: '#8d9193'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#d2e4fb'
  primary-fixed-dim: '#b7c8de'
  on-primary-fixed: '#0b1d2d'
  on-primary-fixed-variant: '#38485a'
  secondary-fixed: '#c6e7ff'
  secondary-fixed-dim: '#83cfff'
  on-secondary-fixed: '#001e2d'
  on-secondary-fixed-variant: '#004c6b'
  tertiary-fixed: '#e0e3e5'
  tertiary-fixed-dim: '#c4c7c9'
  on-tertiary-fixed: '#181c1e'
  on-tertiary-fixed-variant: '#434749'
  background: '#f8f9ff'
  on-background: '#0b1c30'
  surface-variant: '#d3e4fe'
typography:
  headline-lg:
    fontFamily: Inter
    fontSize: 40px
    fontWeight: '700'
    lineHeight: 48px
    letterSpacing: -0.02em
  headline-lg-mobile:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 38px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  headline-sm:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-sm:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.05em
  price-display:
    fontFamily: Inter
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 32px
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  base: 4px
  container-max: 1280px
  gutter: 24px
  margin-mobile: 16px
  margin-desktop: 48px
  stack-sm: 8px
  stack-md: 16px
  stack-lg: 32px
---

## Brand & Style

The design system is engineered to evoke a sense of precision, reliability, and kinetic energy. It targets a modern traveler who values time and clarity above all else. The brand personality is "Technological Professionalism"—it is authoritative enough to handle complex logistics but agile enough to feel effortless.

The visual style is **Corporate Modern** with a focus on high-utility minimalism. It leverages a structured grid, generous whitespace to reduce cognitive load during complex bookings, and subtle motion cues that suggest speed and forward momentum. Every element is designed to feel "ready for departure," stripping away decorative fluff in favor of high-performance interface components.

## Colors

The palette is anchored by **Deep Navy (#1A2B3C)**, used for primary navigation and high-priority actions to establish a foundation of trust and institutional stability. **Vibrant Sky Blue (#00A8E8)** serves as the high-visibility accent, reserved for interactive states, progress indicators, and "fast-path" features.

Backgrounds utilize a tiered neutral system: pure white for content surfaces and a soft **Tertiary Grey (#F4F7F9)** for layout grouping. This ensures the UI remains airy and legible even when displaying dense schedule information.

## Typography

This design system utilizes **Inter** exclusively to maintain a systematic and utilitarian feel. The hierarchy is strictly enforced: 
- **Headlines** use tight letter-spacing and heavy weights to command attention.
- **Price Displays** are treated as a specific typographic role, emphasizing the primary decision factor for travelers.
- **Labels** use uppercase tracking for metadata like station codes (e.g., LHR, NYC) to mimic traditional transport signage.

## Layout & Spacing

The layout follows a **Fluid Grid** model with a maximum container width of 1280px. A 12-column system is used for desktop, collapsing to 4 columns on mobile. 

The "Velocity" rhythm is based on a 4px baseline. Vertical stacking (Stack-MD/LG) is used to separate distinct stages of the booking funnel (Search -> Results -> Payment). Schedule cards should use a 24px internal padding to ensure readability of time and platform data.

## Elevation & Depth

Hierarchy is achieved through **Tonal Layers** and extremely crisp, low-opacity shadows. 
- **Level 0 (Base):** Tertiary Grey (#F4F7F9) for the page background.
- **Level 1 (Cards):** Pure White surface with a 1px border (#E2E8F0).
- **Level 2 (Interactive):** When a train schedule is selected or hovered, it gains a soft ambient shadow (0px 10px 15px -3px rgba(26, 43, 60, 0.08)) to appear lifted.
- **Level 3 (Overlays):** Modals and date pickers use a backdrop blur (8px) on the layer below to maintain context while focusing the user.

## Shapes

The shape language is **Soft (0.25rem)**. This slight rounding takes the edge off the "institutional" feel of travel without making the product look too casual or toy-like. 
- **Buttons:** 4px radius (Soft).
- **Input Fields:** 4px radius.
- **Schedule Cards:** 8px radius (Rounded-LG) to distinguish larger content blocks from individual buttons.

## Components

### Search Inputs & Date Pickers
Inputs use a high-contrast white background with a 1px border. The focus state transitions the border to Sky Blue (#00A8E8). Date pickers should emphasize the "Duration" of the trip with a light blue horizontal bar connecting departure and return dates.

### Passenger Selectors
Use a "Stepper" pattern (minus/plus icons) with a clear numeric count in the center. Avoid dropdowns for passenger counts to reduce taps.

### Train Schedule Cards
- **Structure:** Use a three-column horizontal layout for desktop (Time/Route | Duration | Price/Book).
- **Visual Cues:** Use a solid line with dots at either end to represent the journey path.
- **Speed Indicators:** "Fastest" or "Best Value" tags should use the Sky Blue background with white text, positioned in the top-left corner of the card.

### Buttons
- **Primary:** Deep Navy background, white text. High-performance, bold.
- **Secondary:** Transparent background, Sky Blue border and text. Used for "View Details" or "Add Return."
- **Ghost:** No border, Navy text. Used for navigation or cancel actions.

### Progress Tracker
A thin horizontal line at the top of the viewport, using Sky Blue to indicate how far the user has progressed through the booking funnel.