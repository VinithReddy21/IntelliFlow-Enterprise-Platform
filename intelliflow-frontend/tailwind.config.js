/** @type {import('tailwindcss').Config} */
export default {
  darkMode: 'class',
  content: [
    './index.html',
    './src/**/*.{js,ts,jsx,tsx}',
  ],
  theme: {
    extend: {
      colors: {
        background: '#090a0f',
        foreground: '#f4f4f5',
        card: {
          DEFAULT: 'rgba(15, 17, 23, 0.75)',
          foreground: '#f4f4f5',
        },
        popover: {
          DEFAULT: '#0f1117',
          foreground: '#f4f4f5',
        },
        primary: {
          DEFAULT: '#eab308', // Gold Primary
          hover: '#facc15',
          dark: '#ca8a04',
          foreground: '#090a0f',
        },
        gold: {
          50: '#fffbeb',
          100: '#fef3c7',
          200: '#fde68a',
          300: '#fcd34d',
          400: '#facc15',
          500: '#eab308',
          600: '#ca8a04',
          700: '#a16207',
          800: '#854d0e',
          900: '#713f12',
          glow: 'rgba(234, 179, 8, 0.25)',
        },
        accent: {
          DEFAULT: 'rgba(234, 179, 8, 0.15)',
          foreground: '#facc15',
        },
        border: 'rgba(255, 255, 255, 0.08)',
        input: 'rgba(255, 255, 255, 0.08)',
        ring: '#eab308',
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', '-apple-system', 'sans-serif'],
      },
      boxShadow: {
        'gold-glow': '0 0 25px -5px rgba(234, 179, 8, 0.3)',
        'gold-sm': '0 0 10px -2px rgba(234, 179, 8, 0.25)',
        'glass': '0 8px 32px 0 rgba(0, 0, 0, 0.37)',
      },
      backdropBlur: {
        glass: '12px',
      },
    },
  },
  plugins: [],
};
