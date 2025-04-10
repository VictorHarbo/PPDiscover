<template>
  <div class="search-page">
    <h1>Search Documents</h1>
    <div class="search-form">
      <input 
        v-model="searchQuery" 
        @keyup.enter="performSearch"
        placeholder="Enter your search query..."
        class="search-input"
      />
      <div class="toggle-container">
        <button 
          @click="toggleDocumentType" 
          class="toggle-button"
          :class="{ active: documentType === 'salmer' }"
        >
          Salmer
        </button>
        <button 
          @click="toggleDocumentType" 
          class="toggle-button"
          :class="{ active: documentType === 'praedikener' }"
        >
          Prædikener
        </button>
      </div>
      <button @click="performSearch" class="search-button">Search</button>
    </div>

    <div v-if="loading" class="loading">
      Loading...
    </div>

    <div v-if="error" class="error">
      {{ error }}
    </div>

    <div v-if="searchResults.length" class="results">
      <div class="results-summary">
        Found {{ totalResults }} results
      </div>
      <div v-for="(result, index) in searchResults" :key="result.id || index" class="result-item">
        <div class="result-header">
          <h3>{{ getFirstTitle(result) }}</h3>
          <div class="result-id">ID: {{ result.id }}</div>
        </div>


        <div v-if="result.salmeNumre && result.salmeNumre.length" class="psalm-numbers-section">
          <h4>Salme Numre:</h4>
          <div class="psalm-tags">
              <span v-for="(num, nIndex) in result.salmeNumre" :key="nIndex" class="psalm-tag">
                {{ num }}
              </span>
          </div>
        </div>

        <div class="result-content">
          <div v-if="result.titler && result.titler.length" class="titles-section">
            <h4>Salme Titler:</h4>
            <ul>
              <li v-for="(title, tIndex) in result.titler" :key="tIndex">{{ title }}</li>
            </ul>
          </div>
          
          <div v-if="result.tekster && result.tekster.length" class="texts-section">
            <h4>Texts:</h4>
            <ul>
              <li v-for="(text, tIndex) in result.tekster" :key="tIndex">{{ text }}</li>
            </ul>
          </div>
          
          <div v-if="result.fileLocation && result.fileLocation.length" class="file-location-section">
            <h4>File Location:</h4>
            <p>{{ result.fileLocation[0] }}</p>
          </div>

          <div v-if="result.textRow" class="text-row-section">
            <h4>Tekst række:</h4>
            <p>{{ result.textRow }}</p>
          </div>

          <div v-if="result.fileName" class="file-name-section">
            <h4>Filnavn:</h4>
            <p>{{ result.fileName }}</p>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="!loading && !error" class="no-results">
      No results found. Try a different search query.
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import axios from 'axios'
import { API_ENDPOINTS } from '../config'
import { useRoute } from 'vue-router'

const route = useRoute()
const searchQuery = ref('')
const searchResults = ref([])
const totalResults = ref(0)
const loading = ref(false)
const error = ref(null)
const documentType = ref('salmer') // Default to 'salmer'

// Watch for route changes to clear search when navigating to search page
watch(() => route.path, (newPath) => {
  if (newPath === '/search') {
    clearSearch()
  }
})

const clearSearch = () => {
  searchQuery.value = ''
  searchResults.value = []
  totalResults.value = 0
  error.value = null
}

const toggleDocumentType = () => {
  documentType.value = documentType.value === 'salmer' ? 'praedikener' : 'salmer'
}

const getFirstTitle = (result) => {
  if (result.titler && result.titler.length) {
    return result.fileName
  }
  return 'Untitled Document'
}

const performSearch = async () => {
  if (!searchQuery.value.trim()) return

  loading.value = true
  error.value = null
  searchResults.value = []

  try {
    const response = await axios.get(API_ENDPOINTS.SEARCH, {
      params: {
        q: searchQuery.value,
        type: documentType.value
      }
    })
    
    // Extract the documents from the response
    if (response.data && response.data.response) {
      searchResults.value = response.data.response.docs || []
      totalResults.value = response.data.response.numFound || 0
    } else {
      searchResults.value = []
      totalResults.value = 0
    }
  } catch (err) {
    error.value = 'Error performing search. Please try again.'
    console.error('Search error:', err)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.search-page {
  padding: 20px;
}

.search-form {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.search-input {
  flex: 1;
  min-width: 200px;
  padding: 10px;
  border: 1px solid #ffd1dc;
  border-radius: 4px;
  font-size: 16px;
  background: rgba(255, 255, 255, 0.9);
}

.search-input:focus {
  outline: none;
  border-color: #ff6b81;
  box-shadow: 0 0 0 2px rgba(255, 107, 129, 0.2);
}

.toggle-container {
  display: flex;
  border-radius: 4px;
  overflow: hidden;
  border: 1px solid #ffd1dc;
}

.toggle-button {
  padding: 10px 15px;
  background-color: white;
  color: #ff6b81;
  border: none;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.3s ease;
}

.toggle-button:first-child {
  border-right: 1px solid #ffd1dc;
}

.toggle-button.active {
  background-color: #ff6b81;
  color: white;
}

.search-button {
  padding: 10px 20px;
  background-color: #ff6b81;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s ease;
}

.search-button:hover {
  background-color: #ff4757;
}

.loading {
  text-align: center;
  padding: 20px;
  color: #ff6b81;
}

.error {
  color: #ff4757;
  padding: 10px;
  margin: 10px 0;
  border: 1px solid #ff4757;
  border-radius: 4px;
  background: rgba(255, 71, 87, 0.1);
}

.results-summary {
  margin-bottom: 20px;
  color: #ff6b81;
  font-size: 14px;
}

.result-item {
  padding: 20px;
  margin-bottom: 20px;
  border: 1px solid #ffd1dc;
  border-radius: 8px;
  background-color: white;
  box-shadow: 0 2px 4px rgba(255, 182, 193, 0.1);
  transition: transform 0.3s ease;
}

.result-item:hover {
  transform: translateY(-2px);
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ffd1dc;
}

.result-header h3 {
  margin: 0;
  color: #ff4757;
}

.result-id {
  font-size: 12px;
  color: #ff6b81;
}

.result-content {
  display: grid;
  grid-template-columns: 1fr;
  gap: 15px;
}

.titles-section, .texts-section, .psalm-numbers-section, .file-location-section, .text-row-section, .file-name-section {
  margin-bottom: 10px;
}

h4 {
  margin: 0 0 8px 0;
  color: #ff6b81;
  font-size: 16px;
}

ul {
  margin: 0;
  padding-left: 20px;
}

li {
  margin-bottom: 5px;
  color: #ff4757;
}

.psalm-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.psalm-tag {
  background-color: #ffd1dc;
  color: #ff4757;
  padding: 3px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.no-results {
  text-align: center;
  padding: 20px;
  color: #ff6b81;
  font-style: italic;
}
</style> 