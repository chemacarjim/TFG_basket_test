/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{vue,js,ts}"],
  theme: {
    extend: {
      colors: {
      'brand-blue': '#1e3a8a',
      'brand-purple': '#6d28d9',
      },
    },
  },
  plugins: [],
}

