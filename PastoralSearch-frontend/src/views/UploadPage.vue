<template>
  <div class="upload-page">
    <h1>Upload Documents</h1>
    
    <div class="upload-mode-toggle">
      <label class="toggle-label">
        <span>Multiple File Upload</span>
        <div class="toggle-switch">
          <input type="checkbox" v-model="isMultipleUpload">
          <span class="toggle-slider"></span>
        </div>
      </label>
    </div>

    <div class="document-type-toggle">
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
    </div>

    <div v-if="documentType === 'salmer'" class="collection-selector">
      <label for="collection">Collection:</label>
      <select id="collection" v-model="selectedCollection" class="collection-dropdown">
        <option value="firstRow">FØRSTE RÆKKE</option>
        <option value="secondRow">ANDEN RÆKKE</option>
      </select>
    </div>

    <div class="upload-area" 
         @dragover.prevent 
         @drop.prevent="handleDrop"
         :class="{ 'dragging': isDragging }"
         @dragenter.prevent="isDragging = true"
         @dragleave.prevent="isDragging = false">
      <input 
        type="file" 
        ref="fileInput"
        @change="handleFileSelect"
        multiple
        class="file-input"
      />
      <div class="upload-content">
        <div class="upload-icon">📁</div>
        <p>Drag and drop files here or click to select</p>
        <button @click="triggerFileInput" class="select-button">Select Files</button>
      </div>
    </div>

    <div v-if="uploading" class="upload-status">
      <div class="progress-bar">
        <div class="progress" :style="{ width: uploadProgress + '%' }"></div>
      </div>
      <p>Uploading... {{ uploadProgress }}%</p>
    </div>

    <div v-if="error" class="error">
      {{ error }}
    </div>

    <div v-if="successMessage" class="success">
      {{ successMessage }}
    </div>

    <div v-if="selectedFiles.length" class="selected-files">
      <h3>Selected Files:</h3>
      <ul>
        <li v-for="(file, index) in selectedFiles" :key="index">
          {{ file.name }}
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import axios from 'axios'
import { API_ENDPOINTS } from '../config'

const fileInput = ref(null)
const isDragging = ref(false)
const uploading = ref(false)
const uploadProgress = ref(0)
const error = ref(null)
const successMessage = ref(null)
const selectedFiles = ref([])
const selectedCollection = ref('firstRow')
const isMultipleUpload = ref(false)
const documentType = ref('salmer') // Default to 'salmer'

// Watch for document type changes to reset collection if needed
watch(documentType, (newType) => {
  if (newType === 'praedikener') {
    // Reset collection to default when switching to praedikener
    selectedCollection.value = 'firstRow'
  }
})

const toggleDocumentType = () => {
  documentType.value = documentType.value === 'salmer' ? 'praedikener' : 'salmer'
}

const triggerFileInput = () => {
  fileInput.value.click()
}

const handleFileSelect = (event) => {
  const files = Array.from(event.target.files)
  selectedFiles.value = files
  uploadFiles(files)
}

const handleDrop = (event) => {
  isDragging.value = false
  const files = Array.from(event.dataTransfer.files)
  selectedFiles.value = files
  uploadFiles(files)
}

const uploadFiles = async (files) => {
  if (!files.length) return

  uploading.value = true
  error.value = null
  successMessage.value = null
  uploadProgress.value = 0

  try {
    if (isMultipleUpload.value) {
      // Multiple file upload
      const formData = new FormData()
      files.forEach(file => {
        formData.append('files', file)
      })
      
      // Only append collection if document type is salmer
      if (documentType.value === 'salmer') {
        formData.append('collection', selectedCollection.value)
      }
      
      formData.append('type', documentType.value)

      await axios.post(API_ENDPOINTS.ADD_MULTIPLE, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        },
        onUploadProgress: (progressEvent) => {
          uploadProgress.value = Math.round(
            (progressEvent.loaded * 100) / progressEvent.total
          )
        }
      })
    } else {
      // Individual file upload
      for (const file of files) {
        const formData = new FormData()
        formData.append('file', file)
        
        // Only append collection if document type is salmer
        if (documentType.value === 'salmer') {
          formData.append('collection', selectedCollection.value)
        }
        
        formData.append('type', documentType.value)

        await axios.post(API_ENDPOINTS.UPLOAD, formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          },
          onUploadProgress: (progressEvent) => {
            uploadProgress.value = Math.round(
              (progressEvent.loaded * 100) / progressEvent.total
            )
          }
        })
      }
    }

    successMessage.value = 'Files uploaded successfully!'
    selectedFiles.value = []
  } catch (err) {
    error.value = 'Error uploading files. Please try again.'
    console.error('Upload error:', err)
  } finally {
    uploading.value = false
  }
}
</script>

<style scoped>
.upload-page {
  padding: 20px;
}

.upload-mode-toggle {
  margin-bottom: 20px;
}

.toggle-label {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  color: #ff6b81;
}

.toggle-switch {
  position: relative;
  display: inline-block;
  width: 60px;
  height: 34px;
}

.toggle-switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.toggle-slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ffd1dc;
  transition: .4s;
  border-radius: 34px;
}

.toggle-slider:before {
  position: absolute;
  content: "";
  height: 26px;
  width: 26px;
  left: 4px;
  bottom: 4px;
  background-color: white;
  transition: .4s;
  border-radius: 50%;
}

input:checked + .toggle-slider {
  background-color: #ff6b81;
}

input:checked + .toggle-slider:before {
  transform: translateX(26px);
}

.document-type-toggle {
  margin-bottom: 20px;
}

.toggle-container {
  display: flex;
  border-radius: 4px;
  overflow: hidden;
  border: 1px solid #ffd1dc;
  width: fit-content;
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

.collection-selector {
  margin-bottom: 20px;
}

.collection-dropdown {
  margin-left: 10px;
  padding: 8px;
  border: 1px solid #ffd1dc;
  border-radius: 4px;
  font-size: 16px;
  min-width: 200px;
  background: rgba(255, 255, 255, 0.9);
}

.collection-dropdown:focus {
  outline: none;
  border-color: #ff6b81;
  box-shadow: 0 0 0 2px rgba(255, 107, 129, 0.2);
}

.upload-area {
  border: 2px dashed #ffd1dc;
  border-radius: 8px;
  padding: 40px;
  text-align: center;
  margin: 20px 0;
  position: relative;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.9);
}

.upload-area.dragging {
  border-color: #ff6b81;
  background-color: rgba(255, 209, 220, 0.1);
}

.file-input {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  opacity: 0;
  cursor: pointer;
}

.upload-content {
  pointer-events: none;
}

.upload-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.select-button {
  margin-top: 15px;
  padding: 10px 20px;
  background-color: #ff6b81;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s ease;
  pointer-events: auto;
}

.select-button:hover {
  background-color: #ff4757;
}

.upload-status {
  margin: 20px 0;
}

.progress-bar {
  height: 10px;
  background-color: #ffd1dc;
  border-radius: 5px;
  overflow: hidden;
  margin-bottom: 10px;
}

.progress {
  height: 100%;
  background-color: #ff6b81;
  transition: width 0.3s ease;
}

.error {
  color: #ff4757;
  padding: 10px;
  margin: 10px 0;
  border: 1px solid #ff4757;
  border-radius: 4px;
  background: rgba(255, 71, 87, 0.1);
}

.success {
  color: #2ecc71;
  padding: 10px;
  margin: 10px 0;
  border: 1px solid #2ecc71;
  border-radius: 4px;
  background: rgba(46, 204, 113, 0.1);
}

.selected-files {
  margin-top: 20px;
  padding: 15px;
  border: 1px solid #ffd1dc;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.9);
}

.selected-files h3 {
  margin-top: 0;
  color: #ff6b81;
}

.selected-files ul {
  margin: 0;
  padding-left: 20px;
}

.selected-files li {
  margin-bottom: 5px;
  color: #ff4757;
}
</style> 