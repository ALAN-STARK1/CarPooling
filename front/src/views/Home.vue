<script setup>
import { computed, ref } from "vue"
import { suggestPlaces } from "@/api/map"
import {baiduMapService} from "@/utils/baiduMapService";

const boardingPoint = ref("")
const dropOffPoint = ref("")
const timeRange = ref([])

const selectedProvince1 = ref("")
const selectedCity1 = ref("")
const selectedProvince2 = ref("")
const selectedCity2 = ref("")

const boardingCandidates = ref([])
const dropOffCandidates = ref([])
const boardingExact = ref(null)
const dropOffExact = ref(null)
const boardingSearching = ref(false)
const dropOffSearching = ref(false)

const wayTime = ref(null)

// 用户可调：候选框一次可见条数、每次搜索最多保留条数
const candidateWindowSize = ref(4)
const searchResultLimit = ref(10)

const visibleCandidateCount = computed(() =>
  Math.min(10, Math.max(1, Number(candidateWindowSize.value) || 4))
)
const resultLimitCount = computed(() =>
  Math.min(20, Math.max(1, Number(searchResultLimit.value) || 10))
)
const candidateListStyle = computed(() => ({
  maxHeight: `${visibleCandidateCount.value * 92}px`
}))

const provinces = computed(() =>
  Object.entries(data).map(([code, v]) => ({ code, name: v.name }))
)

const cities1 = computed(() => {
  if (!selectedProvince1.value) return []
  return Object.entries(data[selectedProvince1.value].cities).map(
    ([code, name]) => ({ code, name })
  )
})

const cities2 = computed(() => {
  if (!selectedProvince2.value) return []
  return Object.entries(data[selectedProvince2.value].cities).map(
    ([code, name]) => ({ code, name })
  )
})

const boardingCityName = computed(() => {
  if (!selectedProvince1.value || !selectedCity1.value) return ""
  return data[selectedProvince1.value].cities[selectedCity1.value] || ""
})

const dropOffCityName = computed(() => {
  if (!selectedProvince2.value || !selectedCity2.value) return ""
  return data[selectedProvince2.value].cities[selectedCity2.value] || ""
})

function clearBoardingSelection() {
  boardingCandidates.value = []
  boardingExact.value = null
}

function clearDropOffSelection() {
  dropOffCandidates.value = []
  dropOffExact.value = null
}

function onProvince1Change() {
  selectedCity1.value = ""
  clearBoardingSelection()
}

function onProvince2Change() {
  selectedCity2.value = ""
  clearDropOffSelection()
}

function normalizeSettings() {
  candidateWindowSize.value = visibleCandidateCount.value
  searchResultLimit.value = resultLimitCount.value
}

async function calculateDistance() {
  if (!boardingExact.value || !dropOffExact.value) {
    alert('请先从候选列表中选定起点和终点')
    return
  }
  try {
    wayTime.value = await baiduMapService.getEstimatedDuration(
      boardingExact.value,
      dropOffExact.value
    )
  } catch (e) {
    wayTime.value = null
    alert(e.message || '预估时长计算失败')
  }
}

async function searchPlace(pointRef, cityName, candidatesRef, exactRef, searchingRef, label) {
  if (!cityName) {
    alert(`请先选择${label}所在省市`)
    return
  }
  const keyword = pointRef.value.trim()
  if (!keyword) {
    alert(`请输入${label}`)
    return
  }

  searchingRef.value = true
  exactRef.value = null
  candidatesRef.value = []
  normalizeSettings()

  try {
    const res = await suggestPlaces(keyword, cityName)
    const payload = res.data
    const places = payload?.success && Array.isArray(payload.data) ? payload.data : []
    candidatesRef.value = places.slice(0, resultLimitCount.value)
    if (!candidatesRef.value.length) {
      alert(`未在「${cityName}」找到匹配地点，请换关键词重试`)
    }
  } catch (e) {
    candidatesRef.value = []
    alert(`${label}搜索失败，请稍后重试`)
  } finally {
    searchingRef.value = false
  }
}

function searchBoardingPlace() {
  return searchPlace(
    boardingPoint,
    boardingCityName.value,
    boardingCandidates,
    boardingExact,
    boardingSearching,
    "起点"
  )
}

function searchDropOffPlace() {
  return searchPlace(
    dropOffPoint,
    dropOffCityName.value,
    dropOffCandidates,
    dropOffExact,
    dropOffSearching,
    "终点"
  )
}

function toExactPlace(item) {
  return {
    name: item.name,
    address: item.address,
    lng: item.lng,
    lat: item.lat,
    city: item.city
  }
}

function selectBoardingPlace(item) {
  boardingExact.value = toExactPlace(item)
  boardingPoint.value = item.name
}

function selectDropOffPlace(item) {
  dropOffExact.value = toExactPlace(item)
  dropOffPoint.value = item.name
}


const data = {
  11: { name: '北京', cities: { 1101: '北京市' } },
  12: { name: '天津', cities: { 1201: '天津市' } },
  13: {
    name: '河北',
    cities: {
      1301: '石家庄',
      1302: '唐山',
      1303: '秦皇岛',
      1304: '邯郸',
      1305: '邢台',
      1306: '保定',
      1307: '张家口',
      1308: '承德',
      1309: '沧州',
      1310: '廊坊',
      1311: '衡水'
    }
  },
  14: {
    name: '山西',
    cities: {
      1401: '太原',
      1402: '大同',
      1403: '阳泉',
      1404: '长治',
      1405: '晋城',
      1406: '朔州',
      1407: '晋中',
      1408: '运城',
      1409: '忻州',
      1410: '临汾',
      1411: '吕梁'
    }
  },
  15: {
    name: '内蒙古',
    cities: {
      1501: '呼和浩特',
      1502: '包头',
      1503: '乌海',
      1504: '赤峰',
      1505: '通辽',
      1506: '鄂尔多斯',
      1507: '呼伦贝尔',
      1508: '巴彦淖尔',
      1509: '乌兰察布'
    }
  },
  21: {
    name: '辽宁',
    cities: {
      2101: '沈阳',
      2102: '大连',
      2103: '鞍山',
      2104: '抚顺',
      2105: '本溪',
      2106: '丹东',
      2107: '锦州',
      2108: '营口',
      2109: '阜新',
      2110: '辽阳',
      2111: '盘锦',
      2112: '铁岭',
      2113: '朝阳',
      2114: '葫芦岛'
    }
  },
  22: {
    name: '吉林',
    cities: {
      2201: '长春',
      2202: '吉林',
      2203: '四平',
      2204: '辽源',
      2205: '通化',
      2206: '白山',
      2207: '松原',
      2208: '白城'
    }
  },
  23: {
    name: '黑龙江',
    cities: {
      2301: '哈尔滨',
      2302: '齐齐哈尔',
      2303: '鸡西',
      2304: '鹤岗',
      2305: '双鸭山',
      2306: '大庆',
      2307: '伊春',
      2308: '佳木斯',
      2309: '七台河',
      2310: '牡丹江',
      2311: '黑河',
      2312: '绥化'
    }
  },
  31: { name: '上海', cities: { 3101: '上海市' } },
  32: {
    name: '江苏',
    cities: {
      3201: '南京',
      3202: '无锡',
      3203: '徐州',
      3204: '常州',
      3205: '苏州',
      3206: '南通',
      3207: '连云港',
      3208: '淮安',
      3209: '盐城',
      3210: '扬州',
      3211: '镇江',
      3212: '泰州',
      3213: '宿迁'
    }
  },
  33: {
    name: '浙江',
    cities: {
      3301: '杭州',
      3302: '宁波',
      3303: '温州',
      3304: '嘉兴',
      3305: '湖州',
      3306: '绍兴',
      3307: '金华',
      3308: '衢州',
      3309: '舟山',
      3310: '台州',
      3311: '丽水'
    }
  },
  34: {
    name: '安徽',
    cities: {
      3401: '合肥',
      3402: '芜湖',
      3403: '蚌埠',
      3404: '淮南',
      3405: '马鞍山',
      3406: '淮北',
      3407: '铜陵',
      3408: '安庆',
      3410: '黄山',
      3411: '滁州',
      3412: '阜阳',
      3413: '宿州',
      3415: '六安',
      3416: '亳州',
      3417: '池州',
      3418: '宣城'
    }
  },
  35: {
    name: '福建',
    cities: {
      3501: '福州',
      3502: '厦门',
      3503: '莆田',
      3504: '三明',
      3505: '泉州',
      3506: '漳州',
      3507: '南平',
      3508: '龙岩',
      3509: '宁德'
    }
  },
  36: {
    name: '江西',
    cities: {
      3601: '南昌',
      3602: '景德镇',
      3603: '萍乡',
      3604: '九江',
      3605: '新余',
      3606: '鹰潭',
      3607: '赣州',
      3608: '吉安',
      3609: '宜春',
      3610: '抚州',
      3611: '上饶'
    }
  },
  37: {
    name: '山东',
    cities: {
      3701: '济南',
      3702: '青岛',
      3703: '淄博',
      3704: '枣庄',
      3705: '东营',
      3706: '烟台',
      3707: '潍坊',
      3708: '济宁',
      3709: '泰安',
      3710: '威海',
      3711: '日照',
      3713: '临沂',
      3714: '德州',
      3715: '聊城',
      3716: '滨州',
      3717: '菏泽'
    }
  },
  41: {
    name: '河南',
    cities: {
      4101: '郑州',
      4102: '开封',
      4103: '洛阳',
      4104: '平顶山',
      4105: '安阳',
      4106: '鹤壁',
      4107: '新乡',
      4108: '焦作',
      4109: '濮阳',
      4110: '许昌',
      4111: '漯河',
      4112: '三门峡',
      4113: '南阳',
      4114: '商丘',
      4115: '信阳',
      4116: '周口',
      4117: '驻马店'
    }
  },
  42: {
    name: '湖北',
    cities: {
      4201: '武汉',
      4202: '黄石',
      4203: '十堰',
      4205: '宜昌',
      4206: '襄阳',
      4207: '鄂州',
      4208: '荆门',
      4209: '孝感',
      4210: '荆州',
      4211: '黄冈',
      4212: '咸宁',
      4213: '随州'
    }
  },
  43: {
    name: '湖南',
    cities: {
      4301: '长沙',
      4302: '株洲',
      4303: '湘潭',
      4304: '衡阳',
      4305: '邵阳',
      4306: '岳阳',
      4307: '常德',
      4308: '张家界',
      4309: '益阳',
      4310: '郴州',
      4311: '永州',
      4312: '怀化',
      4313: '娄底',
      4331: '湘西'
    }
  },
  44: {
    name: '广东',
    cities: {
      4401: '广州',
      4402: '韶关',
      4403: '深圳',
      4404: '珠海',
      4405: '汕头',
      4406: '佛山',
      4407: '江门',
      4408: '湛江',
      4409: '茂名',
      4412: '肇庆',
      4413: '惠州',
      4414: '梅州',
      4415: '汕尾',
      4416: '河源',
      4417: '阳江',
      4418: '清远',
      4419: '东莞',
      4420: '中山',
      4451: '潮州',
      4452: '揭阳',
      4453: '云浮'
    }
  },
  45: {
    name: '广西',
    cities: {
      4501: '南宁',
      4502: '柳州',
      4503: '桂林',
      4504: '梧州',
      4505: '北海',
      4506: '防城港',
      4507: '钦州',
      4508: '贵港',
      4509: '玉林',
      4510: '百色',
      4511: '贺州',
      4512: '河池',
      4513: '来宾',
      4514: '崇左'
    }
  },
  46: {
    name: '海南',
    cities: {
      4601: '海口',
      4602: '三亚',
      4603: '三沙',
      4604: '儋州'
    }
  },
  50: { name: '重庆', cities: { 5001: '重庆市' } },
  51: {
    name: '四川',
    cities: {
      5101: '成都',
      5103: '自贡',
      5104: '攀枝花',
      5105: '泸州',
      5106: '德阳',
      5107: '绵阳',
      5108: '广元',
      5109: '遂宁',
      5110: '内江',
      5111: '乐山',
      5113: '南充',
      5114: '眉山',
      5115: '宜宾',
      5116: '广安',
      5117: '达州',
      5118: '雅安',
      5119: '巴中',
      5120: '资阳'
    }
  },
  52: {
    name: '贵州',
    cities: {
      5201: '贵阳',
      5202: '六盘水',
      5203: '遵义',
      5204: '安顺',
      5205: '毕节',
      5206: '铜仁'
    }
  },
  53: {
    name: '云南',
    cities: {
      5301: '昆明',
      5303: '曲靖',
      5304: '玉溪',
      5305: '保山',
      5306: '昭通',
      5307: '丽江',
      5308: '普洱',
      5309: '临沧'
    }
  },
  54: {
    name: '西藏',
    cities: {
      5401: '拉萨',
      5402: '日喀则',
      5403: '昌都',
      5404: '林芝',
      5405: '山南',
      5406: '那曲',
      5407: '阿里'
    }
  },
  61: {
    name: '陕西',
    cities: {
      6101: '西安',
      6102: '铜川',
      6103: '宝鸡',
      6104: '咸阳',
      6105: '渭南',
      6106: '延安',
      6107: '汉中',
      6108: '榆林',
      6109: '安康',
      6110: '商洛'
    }
  },
  62: {
    name: '甘肃',
    cities: {
      6201: '兰州',
      6202: '嘉峪关',
      6203: '金昌',
      6204: '白银',
      6205: '天水',
      6206: '武威',
      6207: '张掖',
      6208: '平凉',
      6209: '酒泉',
      6210: '庆阳',
      6211: '定西',
      6212: '陇南'
    }
  },
  63: {
    name: '青海',
    cities: {
      6301: '西宁',
      6302: '海东'
    }
  },
  64: {
    name: '宁夏',
    cities: {
      6401: '银川',
      6402: '石嘴山',
      6403: '吴忠',
      6404: '固原',
      6405: '中卫'
    }
  },
  65: {
    name: '新疆',
    cities: {
      6501: '乌鲁木齐',
      6502: '克拉玛依',
      6504: '吐鲁番',
      6505: '哈密'
    }
  },
  71: { name: '台湾', cities: { 7101: '台北市' } },
  81: { name: '香港', cities: { 8101: '香港' } },
  82: { name: '澳门', cities: { 8201: '澳门' } }
}

</script>

<template>
  <div class="home-page">
    <div class="candidate-settings">
      <label>
        候选框可见条数
        <input
          v-model.number="candidateWindowSize"
          type="number"
          min="1"
          max="10"
          @change="normalizeSettings"
        />
      </label>
      <label>
        单次结果上限
        <input
          v-model.number="searchResultLimit"
          type="number"
          min="1"
          max="20"
          @change="normalizeSettings"
        />
      </label>
    </div>

    <section class="place-section">
      <h3>选择起点</h3>
    <div class="selector1">
      <select v-model="selectedProvince1" @change="onProvince1Change">
        <option value="">请选择省份</option>
        <option v-for="p in provinces" :key="p.code" :value="p.code">
          {{ p.name }}
        </option>
      </select>

      <select v-model="selectedCity1" :disabled="!cities1.length" @change="clearBoardingSelection">
        <option value="">请选择城市</option>
        <option v-for="c in cities1" :key="c.code" :value="c.code">
          {{ c.name }}
        </option>
      </select>
    </div>

    <input
      v-model="boardingPoint"
      :disabled="!cities1.length"
      placeholder="请输入上车点"
      @input="boardingExact = null"
    />

    <button
      type="button"
      :disabled="!boardingCityName || !boardingPoint.trim() || boardingSearching"
      @click="searchBoardingPlace"
    >
      {{ boardingSearching ? '搜索中...' : '搜索地点' }}
    </button>

    <ul
      v-if="boardingCandidates.length"
      class="candidate-list"
      :style="candidateListStyle"
    >
      <li
        v-for="(item, i) in boardingCandidates"
        :key="`${item.lng}-${item.lat}-${i}`"
        :class="{ selected: boardingExact?.lng === item.lng && boardingExact?.lat === item.lat }"
        @click="selectBoardingPlace(item)"
      >
        <strong>{{ item.name }}</strong>
        <span>{{ item.address }}</span>
        <small>{{ item.lng }}, {{ item.lat }}</small>
      </li>
    </ul>

    <p v-if="boardingExact" class="selected-tip">
      已选起点：{{ boardingExact.name }}
      （{{ boardingExact.lng }}, {{ boardingExact.lat }}）
    </p>
    </section>

    <section class="place-section">
      <h3>选择终点</h3>
    <div class="selector2">
      <select v-model="selectedProvince2" @change="onProvince2Change">
        <option value="">请选择省份</option>
        <option v-for="p in provinces" :key="p.code" :value="p.code">
          {{ p.name }}
        </option>
      </select>

      <select v-model="selectedCity2" :disabled="!cities2.length" @change="clearDropOffSelection">
        <option value="">请选择城市</option>
        <option v-for="c in cities2" :key="c.code" :value="c.code">
          {{ c.name }}
        </option>
      </select>
    </div>

    <input
      v-model="dropOffPoint"
      :disabled="!cities2.length"
      placeholder="请输入终点"
      @input="dropOffExact = null"
    />

    <button
      type="button"
      :disabled="!dropOffCityName || !dropOffPoint.trim() || dropOffSearching"
      @click="searchDropOffPlace"
    >
      {{ dropOffSearching ? '搜索中...' : '搜索地点' }}
    </button>

    <button
      type="button"
      :disabled="!boardingExact || !dropOffExact"
      @click="calculateDistance"
    >
      计算预估时长
    </button>

    <p v-if="wayTime" class="selected-tip">
      预估到达时长：{{ wayTime.durationText }}
      （约 {{ wayTime.durationMinutes }} 分钟 / {{ wayTime.durationSeconds }} 秒）
    </p>

    <ul
      v-if="dropOffCandidates.length"
      class="candidate-list"
      :style="candidateListStyle"
    >
      <li
        v-for="(item, i) in dropOffCandidates"
        :key="`${item.lng}-${item.lat}-${i}`"
        :class="{ selected: dropOffExact?.lng === item.lng && dropOffExact?.lat === item.lat }"
        @click="selectDropOffPlace(item)"
      >
        <strong>{{ item.name }}</strong>
        <span>{{ item.address }}</span>
        <small>{{ item.lng }}, {{ item.lat }}</small>
      </li>
    </ul>

    <p v-if="dropOffExact" class="selected-tip">
      已选终点：{{ dropOffExact.name }}
      （{{ dropOffExact.lng }}, {{ dropOffExact.lat }}）
    </p>
    </section>

    <div class="timeRange">
      <el-date-picker
        v-model="timeRange"
        type="datetimerange"
        start-placeholder="开始时间"
        end-placeholder="结束时间"
        format="YYYY-MM-DD HH:mm:ss"
        value-format="YYYY-MM-DD HH:mm:ss"
      />
    </div>
  </div>
</template>

<style scoped>
.home-page {
  width: min(720px, calc(100% - 32px));
  margin: 24px auto;
  color: #1f2937;
}

.candidate-settings,
.place-section {
  margin-bottom: 18px;
  padding: 16px;
  border: 1px solid #dbe3ee;
  border-radius: 10px;
  background: #fff;
}

.candidate-settings {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.candidate-settings label {
  display: flex;
  align-items: center;
  gap: 8px;
}

.candidate-settings input {
  width: 64px;
}

.place-section h3 {
  margin: 0 0 12px;
}

.selector1,
.selector2 {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.place-section > input {
  width: min(420px, 65%);
  margin-right: 8px;
}

select,
input,
button {
  min-height: 34px;
  box-sizing: border-box;
}

.candidate-list {
  margin: 12px 0 0;
  padding: 0;
  overflow-y: auto;
  overscroll-behavior: contain;
  border: 1px solid #d7dee8;
  border-radius: 8px;
  background: #fff;
}

.candidate-list li {
  min-height: 92px;
  padding: 10px 12px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 4px;
  border-bottom: 1px solid #edf1f5;
  cursor: pointer;
}

.candidate-list li:last-child {
  border-bottom: 0;
}

.candidate-list li:hover,
.candidate-list li.selected {
  background: #edf6ff;
}

.candidate-list span,
.candidate-list small {
  color: #64748b;
}

.selected-tip {
  margin-bottom: 0;
  color: #087f5b;
}
</style>