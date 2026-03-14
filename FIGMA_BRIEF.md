# Figma AI Design Brief: Cave Runner

---

## 1. Art Style & Color Palette

**Art Style:** Pixel art with a bold, arcade aesthetic. Use 16×16 or 32×32 px grid-aligned sprites with clean outlines and minimal anti-aliasing. The overall mood is retro-casual, playful, and energetic—reminiscent of classic mobile endless runners but with modern color saturation and smooth animations. Characters and obstacles should be immediately readable at mobile screen size.

**Primary Color Palette:**
- Crystal Biome: `#4DD0E1` (cyan), `#80DEEA` (light cyan), `#26C6DA` (darker cyan)
- Lava Biome: `#FF6F00` (deep orange), `#FF9100` (bright orange), `#D84315` (dark red-orange)
- Ice Biome: `#B3E5FC` (pale blue), `#81D4FA` (sky blue), `#0277BD` (deep blue)

**Accent Colors:**
- Coin Gold: `#FFD700` (bright gold)
- Danger/Hazard: `#FF1744` (neon red)

**Typography:** Use a bold, geometric sans-serif font (e.g., Roboto Bold, Nunito Bold, or Press Start 2P for retro flavor). All UI text should have crisp 2–3px drop shadows in dark gray (`#212121`) to pop against varied backgrounds. Headers use 48–56px weight; body text uses 24–32px.

---

## 2. App Icon — icon_512.png (512×512px)

**Background:** Radial gradient from `#FF6F00` (center) to `#D84315` (edges), creating a warm, energetic lava-inspired glow. Add a subtle radial vignette with dark semi-transparent overlay (`#1A0000` at 20% opacity) around outer edges.

**Central Symbol:** A stylized pixel-art character in mid-jump pose (arms up, one leg extended), rendered in bright `#4DD0E1` with a white outline (3px stroke). Behind the character, place two staggered cave opening silhouettes in `#212121` to suggest depth and the "cave" theme. Add a small `#FFD700` coin floating near the character's hand.

**Effects:** Apply a soft inner shadow (dark `#1A0000` at 15% opacity, 8px blur) around the inner edges to create dimensional depth. Add a subtle glow halo around the character in `#FFD700` (4px blur, 10% opacity) to make it pop. Include small sparkle accents (3–4 bright white `#FFFFFF` dots, 4–6px radius) scattered around the character.

**Safe Zone:** Keep the character and primary symbol within the central 400×400px area; cave silhouettes may extend slightly beyond but remain visible and not cut off.

**Overall Mood:** Bold, energetic, arcade-inspired; instantly communicates jumping, movement, and cave exploration.

---

## 3. Backgrounds (854×480 landscape)

### Background File List (derived from game description):
1. `backgrounds/bg_main.png` – Main menu / title screen
2. `backgrounds/bg_crystal_cave.png` – Crystal biome environment
3. `backgrounds/bg_lava_cave.png` – Lava biome environment
4. `backgrounds/bg_ice_cave.png` – Ice biome environment
5. `backgrounds/bg_biome_select.png` – Biome selection screen backdrop

---

### Background Descriptions

**backgrounds/bg_main.png (854×480)**
A stylized cave entrance view with a mid-ground perspective, featuring a gradient sky transitioning from `#1A237E` (deep indigo, top) to `#0D47A1` (dark blue, bottom). The cave mouth is rendered in layered silhouettes: dark rocky outcrops in `#212121` frame the left and right edges, with a subtle stone texture (diagonal cross-hatch pattern, 2px lines, 30% opacity in `#424242`). Place three glowing orbs (one per biome color: cyan, orange, blue) floating in the cave background at varying depths, each with a soft radial glow (12px blur). This creates mystique and hints at the three biomes. Include a subtle parallax-ready cave ceiling with hanging stalactites as dark shapes (`#0D0D0D`). Overall mood: mysterious, inviting, arcade-adventure.

**backgrounds/bg_crystal_cave.png (854×480)**
A seamless parallax-friendly cave corridor rendered in three layers: far-back cave wall with tiled crystalline formations in light `#80DEEA` with dark `#004D7A` outlines (3px), creating a faceted gem-like pattern; mid-ground darker rock face in `#1A237E` with scattered bright cyan `#4DD0E1` crystal clusters embedded (10–20 per screen, 16–32px wide); foreground ground plane in muted `#424242` with cyan-glowing gem fragments (`#4DD0E1` with 8px glow). Add floating dust particles (tiny circles, `#FFFFFF` at 10% opacity, sparse) for depth. Overall mood: cool, crystalline, safe and inviting.

**backgrounds/bg_lava_cave.png (854×480)**
A hot, dangerous cave corridor with active lava: the far-back wall is a gradient from `#BF360C` (dark red) to `#FF6F00` (orange), rendered with jagged, irregular stone shapes. Mid-ground features slow-moving lava flows as wavy horizontal bands in `#FF9100` with darker `#E65100` outlines and small glowing "bubbles" (`#FFEB3B` at 20% opacity, 4px glow) rising from the surface. The foreground ground plane is `#424242` (dark basalt) with orange-red cracks (`#FF6F00`, 2px lines) and occasional flickering embers (tiny triangles, `#FFD700`, scattered). Add subtle heat wave distortion lines (sine curves, `#FF6F00` at 5% opacity) drifting upward. Overall mood: intense, dangerous, energetic.

**backgrounds/bg_ice_cave.png (854×480)**
A frozen, crystalline cave rendered in cool blues and whites: far-back cave wall is a gradient from `#E1F5FE` (light ice) to `#0277BD` (deep ice blue), with vertical icicle shapes in `#B3E5FC` outlined in `#01579B` (3px). Mid-ground features hanging icicles and frozen formations at varying depths, some with a frosted translucent effect. Foreground ground plane is `#90CAF9` (bright ice) with dark blue cracks (`#1565C0`, 2px) and scattered snow drifts as white patches (`#FFFFFF` at 60% opacity). Add subtle frozen mist particles (circles, `#E1F5FE` at 15% opacity, floating slowly). Include faint aurora-like light beams (vertical gradients, `#81D4FA` to transparent, 30% opacity) in the background for magical ambiance. Overall mood: serene, slippery, cool and mysterious.

**backgrounds/bg_biome_select.png (854×480)**
A stylized "cave hub" showing three tunnel entrances side by side, each tinted with its respective biome color: left tunnel entrance framed in cyan (`#4DD0E1`), center in orange (`#FF6F00`), right in blue (`#0277BD`). The background is a neutral gradient from `#424242` (bottom) to `#616161` (top), representing the main cavern. Place subtle glowing auras around each tunnel entrance (16px blur, matching biome color, 15% opacity) to draw attention. Add small floating particles (2–3px circles) in each tunnel's color, drifting inward to hint at activity. Include a stone archway or decorated frame around the entire composition in `#212121` with a subtle beveled edge effect. Overall mood: welcoming, clearly organized, biome-distinct.

---

## 4. UI Screens (854×480 landscape)

### MainMenuScreen
**Background Used:** `backgrounds/bg_main.png`  
**Header:** "CAVE RUNNER" title in bold 56px font, centered at top (y: 40px), color `#FFD700` with 3px dark shadow (`#212121`). Subtitle "ENDLESS RUNNER" in 24px below, centered, color `#FFFFFF` at 80% opacity.  
**Buttons:** Five buttons arranged vertically, centered, starting at y: 140px with 60px spacing. Each button is 240px wide, 56px tall, with rounded corners (8px). Labels: "PLAY" (primary cyan `#4DD0E1`), "SHOP" (gold `#FFD700`), "LEADERBOARD" (light gray `#BDBDBD`), "SETTINGS" (muted blue `#424242`), "HOW TO PLAY" (bright orange `#FF9100`). Text is 28px bold, centered white. Add a subtle bottom-right coin counter display (y: 460px, right: 20px) showing coin icon and total balance in 20px font.

### BiomeSelectScreen
**Background Used:** `backgrounds/bg_biome_select.png`  
**Header:** "SELECT BIOME" in bold 48px, centered at top (y: 30px), color `#FFFFFF` with dark shadow.  
**Biome Selection Cards:** Three large cards (200px wide, 240px tall) arranged horizontally, centered, starting at y: 100px with 20px gaps. **Left Card (Crystal):** background gradient cyan `#4DD0E1` to `#26C6DA`, label "CRYSTAL" in 32px bold white, difficulty "EASY" in 18px light gray below. **Center Card (Lava):** background gradient orange `#FF6F00` to `#D84315`, label "LAVA" in 32px bold white, difficulty "MEDIUM" in 18px light gray. **Right Card (Ice):** background gradient blue `#0277BD` to `#01579B`, label "ICE" in 32px bold white, difficulty "HARD" in 18px light gray. Each card has a rounded corner (12px) and a subtle drop shadow. Add a "BACK" button (120px wide, 48px tall) at bottom-left (y: 420px, x: 20px), text 24px, color `#BDBDBD`.

### CrystalCaveScreen
**Background Used:** `backgrounds/bg_crystal_cave.png`  
**HUD Elements:** Top-left score display (y: 10px, x: 10px) showing "SCORE: 0000" in 20px monospace white. Top-right coin counter (y: 10px, x: 790px, right-aligned) showing coin icon + amount in 20px white. Bottom center shows swipe hint text "⬆ JUMP  ⬇ SLIDE  ⬅ TURN" in 16px semi-transparent white (40% opacity), positioned y: 450px. Include a faint vignette overlay (dark edges, 10% opacity) to focus attention center-screen. No buttons visible during gameplay; pause/menu access via edge-swipe or system back button.

### LavaCaveScreen
**Background Used:** `backgrounds/bg_lava_cave.png`  
**HUD Elements:** Identical layout to CrystalCaveScreen—top-left score (y: 10px, x: 10px) in white 20px monospace. Top-right coin counter (y: 10px, x: 790px) in white 20px. Bottom-center swipe hints in semi-transparent white (40% opacity). Add a subtle red tint overlay (5% opacity, `#FF1744`) to reinforce danger/heat. Vignette overlay (10% dark edges) for focus.

### IceCaveScreen
**Background Used:** `backgrounds/bg_ice_cave.png`  
**HUD Elements:** Identical layout to CrystalCaveScreen—top-left score (y: 10px, x: 10px) in white 20px monospace. Top-right coin counter (y: 10px, x: 790px) in white 20px. Bottom-center swipe hints in semi-transparent white (40% opacity). Add a subtle blue tint overlay (5% opacity, `#81D4FA`) to reinforce ice/cold. Vignette overlay (10% dark edges) for focus.

### CharacterScreen
**Background Used:** `backgrounds/bg_main.png`  
**Header:** "SHOP" in bold 48px, centered at top (y: 30px), color `#FFD700` with dark shadow. Below header (y: 85px), display "COINS: [amount]" in 24px white, centered.  
**Content Area:** Large central viewport (y: 120–400px, x: 80–774px) showing character model centered with purchased/locked skins displayed as a carousel or grid. Skin cards are 120px wide, 140px tall with rounded corners (8px), spaced 15px apart, showing character preview, skin name, and coin cost or "EQUIPPED" status. Below each skin, a button "BUY" (60px wide, 32px tall, orange `#FF9100`) or "EQUIP" (60px wide, 32px tall, cyan `#4DD0E1`).  
**Bottom Buttons:** "BACK" button (120px wide, 48px tall) at bottom-left (y: 420px, x: 20px), text 24px, color `#BDBDBD`.

### GameOverScreen
**Background:** Semi-transparent dark overlay (`#000000` at 60% opacity) over the last active biome background (e.g., `bg_crystal_cave.png` if player was in Crystal biome).  
**Modal Card:** Centered white card (420px wide, 320px tall, y: 80–400px, rounded 16px), drop shadow (8px blur, 20% opacity).  
**Content:** "GAME OVER" header in bold 44px, centered at top of card (padding-top: 20px), color `#212121`. Below, "SCORE: 0000" in 28px bold, centered. "COINS EARNED: +150" in 20px, centered, color gold `#FFD700`. "BEST SCORE: 5000" in 18px light gray, centered.  
**Buttons:** Two buttons at bottom of card (each 140px wide, 44px tall, spaced 20px). Left button "RESTART" (text 20px, color cyan `#4DD0E1`). Right button "MENU" (text 20px, color orange `#FF9100`).

### LeaderboardScreen
**Background Used:** `backgrounds/bg_main.png`  
**Header:** "LEADERBOARD" in bold 48px, centered at top (y: 30px), color `#FFD700` with dark shadow.  
**Leaderboard List:** Centered list (max 10 entries) starting at y: 100px, each row 32px tall. Row format: rank number (2 chars, right-aligned, 20px monospace, light gray), player name (16 chars, 20px bold white), score (6 chars, right-aligned, 20px bold gold `#FFD700`). Alternating row backgrounds: every other row has a faint tint (`#FFFFFF` at 5% opacity) for scanlines effect. Rank 1 row has a gold background tint (`#FFD700` at 8% opacity). Rank 2 has silver (`#C0C0C0` at 8%), Rank 3 has bronze (`#CD7F32` at 8%).  
**Bottom Button:** "BACK" button (120px wide, 48px tall) at bottom-left (y: 420px, x: 20px), text 24px, color `#BDBDBD`.

### SettingsScreen
**Background Used:** `backgrounds/bg_main.png`  
**Header:** "SETTINGS" in bold 48px, centered at top (y: 30px), color `#FFD700` with dark shadow.  
**Settings List:** Centered column starting at y: 110px, each setting row 70px tall. Three main settings:
1. "MUSIC" toggle (label left-aligned, 20px white; toggle switch right-aligned, 60px wide, y-offset in row center)
2. "VIBRATION" toggle (same layout)
3. "RESET DATA" button (label left-aligned, 20px white; button right-aligned, 80px wide, 40px tall, red `#FF1744`)  

Toggle switches use accent color `#4DD0E1` when ON, muted `#9E9E9E` when OFF. Below settings, add "VERSION 1.0" centered at y: 380px in 16px light gray.  
**Bottom Button:** "BACK" button (120px wide, 48px tall) at bottom-left (y: 420px, x: 20px), text 24px, color `#BDBDBD`.

### HowToPlayScreen
**Background Used:** `backgrounds/bg_main.png`  
**Header:** "HOW TO PLAY" in bold 48px, centered at top (y: 30px), color `#FFD700` with dark shadow.  
**Content Panels:** Centered vertical scrollable or paginated section (starting y: 100px, max width 700px) with three instructional panels, each 140px tall, spaced 20px:
1. **"SWIPE UP TO JUMP"** – Cyan `#4DD0E1` background, icon showing upward arrow, white text 22px bold, description text 16px light gray below: "Avoid low obstacles."
2. **"SWIPE DOWN TO SLIDE"** – Orange `#FF9100` background, downward arrow icon, white text 22px bold, description: "Duck under tall obstacles."
3. **"SWIPE LEFT TO TURN"** – Blue `#0277BD` background, left arrow icon, white text 22px bold, description: "Reverse direction (rare)."

Each panel is rounded (8px) with a subtle shadow. Add small pixel-art icons above text (32×32px, clear and readable).  
**Bottom Button:** "BACK" button (120px wide, 48px tall) at bottom-left (y: 420px, x: 20px), text 24px, color `#BDBDBD`.

---

## 5. Export Checklist

- icon_512.png (512×512)
- backgrounds/bg_main.png (854×480)
- backgrounds/bg_crystal_cave.png (854×480)
- backgrounds/bg_lava_cave.png (854×480)
- backgrounds/bg_ice_cave.png (854×480)
- backgrounds/bg_biome_select.png (854×480)
- ui/mainmenu_screen.png (854×480)
- ui/biome_select_screen.png (854×480)
- ui/game_crystal_cave_screen.png (854×480)
- ui/game_lava_cave_screen.png (854×480)
- ui/game_ice_cave_screen.png (854×480)
- ui/character_screen.png (854×480)
- ui/gameover_screen.png (854×480)
- ui/leaderboard_screen.png (854×480)
- ui/settings_screen.png (854×480)
- ui/howtoplay_screen.png (854×480)
