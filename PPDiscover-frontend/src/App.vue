<template>
  <div class="app">
    <nav class="navigation">
      <router-link to="/search">Search</router-link> |
      <router-link to="/upload">Upload</router-link>
    </nav>
    <main class="content">
      <router-view></router-view>
    </main>
  </div>
</template>

<style>
.app {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: 100vh;
  background: white;
}

.navigation {
  padding: 20px 0;
  margin-bottom: 20px;
  border-bottom: 1px solid #ffd1dc;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(255, 182, 193, 0.1);
}

.navigation a {
  color: #ff6b81;
  text-decoration: none;
  margin: 0 10px;
  transition: color 0.3s ease;
}

.navigation a:hover {
  color: #ff4757;
}

.navigation a.router-link-active {
  color: #ff4757;
  font-weight: bold;
}

.content {
  padding: 20px 0;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(255, 182, 193, 0.1);
}
</style> 