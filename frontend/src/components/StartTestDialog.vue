<script setup lang="ts">
import { ref, watch } from 'vue'

type StartPayload = {
  name: string
  surname: string
  birthDate?: string
  country?: string
  gender?: string
  dominantHand?: string
  position?: string
  inasidnr?: string
  event?: string
  instructor?: string
}

const props = defineProps<{
  visible: boolean
  testTitle?: string
  loading?: boolean
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'start', payload: StartPayload): void
}>()

const dialogRef = ref<HTMLDialogElement | null>(null)
const formError = ref<string | null>(null)

const form = ref<StartPayload>({
  name: '',
  surname: '',
  birthDate: '',
  country: '',
  gender: '',
  dominantHand: '',
  position: '',
  inasidnr: '',
  event: '',
  instructor: '',
})

function resetForm() {
  form.value = {
    name: '',
    surname: '',
    birthDate: '',
    country: '',
    gender: '',
    dominantHand: '',
    position: '',
    inasidnr: '',
    event: '',
    instructor: '',
  }
  formError.value = null
}

watch(
  () => props.visible,
  (v) => {
    if (!dialogRef.value) return
    if (v) {
      resetForm()
      dialogRef.value.showModal()
    } else {
      dialogRef.value.close()
    }
  }
)

function submit() {
  formError.value = null
  if (!form.value.name.trim() || !form.value.surname.trim()) {
    formError.value = 'Nombre y apellidos son obligatorios'
    return
  }
  emit('start', { ...form.value })
}
</script>

<template>
  <dialog ref="dialogRef" class="backdrop:bg-black/60 rounded-xl p-0 w-[min(94vw,76rem)] max-w-none">
    <div class="bg-white rounded-xl p-10">
      <h2 class="text-4xl font-bold text-gray-900">Iniciar test</h2>
      <p class="mt-2 text-lg text-gray-600">
        {{ testTitle ? `Completa los datos para "${testTitle}"` : 'Completa los datos del participante' }}
      </p>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-5 mt-7">
        <div>
          <label class="text-base text-gray-700">Nombre *</label>
          <input v-model="form.name" class="w-full mt-2 p-3 rounded border text-lg" />
        </div>
        <div>
          <label class="text-base text-gray-700">Apellidos *</label>
          <input v-model="form.surname" class="w-full mt-2 p-3 rounded border text-lg" />
        </div>

        <div>
          <label class="text-base text-gray-700">Fecha nacimiento</label>
          <input type="date" v-model="form.birthDate" class="w-full mt-2 p-3 rounded border text-lg" />
        </div>
        <div>
          <label class="text-base text-gray-700">País</label>
          <input v-model="form.country" class="w-full mt-2 p-3 rounded border text-lg" />
        </div>

        <div>
          <label class="text-base text-gray-700">Género</label>
          <select v-model="form.gender" class="w-full mt-2 p-3 rounded border text-lg">
            <option value="">Seleccione género</option>
            <option value="FEMENINO">Femenino</option>
            <option value="MASCULINO">Masculino</option>
            <option value="OTRO">Otro</option>
          </select>
        </div>
        <div>
          <label class="text-base text-gray-700">Mano dominante</label>
          <select v-model="form.dominantHand" class="w-full mt-2 p-3 rounded border text-lg">
            <option value="">Seleccione mano dominante</option>
            <option value="DERECHA">Derecha</option>
            <option value="IZQUIERDA">Izquierda</option>
          </select>
        </div>

        <div>
          <label class="text-base text-gray-700">Posición</label>
          <select v-model="form.position" class="w-full mt-2 p-3 rounded border text-lg">
            <option value="">Seleccione posición</option>
            <option value="BASE">Base</option>
            <option value="ESCOLTA">Escolta</option>
            <option value="ALERO">Alero</option>
            <option value="ALA-PIVOT">Ala-Pivot</option>
            <option value="PIVOT">Pivot</option>
          </select>
        </div>
        <div>
          <label class="text-base text-gray-700">INASIDNR</label>
          <input v-model="form.inasidnr" class="w-full mt-2 p-3 rounded border text-lg" />
        </div>

        <div>
          <label class="text-base text-gray-700">Evento</label>
          <input v-model="form.event" class="w-full mt-2 p-3 rounded border text-lg" />
        </div>
        <div>
          <label class="text-base text-gray-700">Instructor</label>
          <input v-model="form.instructor" class="w-full mt-2 p-3 rounded border text-lg" />
        </div>
      </div>

      <p v-if="formError" class="text-red-600 text-base mt-4">{{ formError }}</p>

      <div class="mt-8 flex justify-end gap-3">
        <button class="px-6 py-3 rounded-xl bg-gray-100 text-lg" @click="emit('close')" :disabled="loading">
          Volver
        </button>
        <button class="px-6 py-3 rounded-xl bg-blue-600 text-white hover:bg-blue-700 text-lg"
                @click="submit"
                :disabled="loading">
          {{ loading ? 'Iniciando...' : 'Iniciar test' }}
        </button>
      </div>
    </div>
  </dialog>
</template>