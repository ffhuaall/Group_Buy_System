<template>
  <div class="group-leader-management">
    <el-card class="page-header">
      <div class="header-content">
        <h2>团长管理</h2>
        <p>管理系统中的所有团长信息和社区分配</p>
      </div>
    </el-card>

    <!-- 搜索和操作栏 -->
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索团长姓名或手机号"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-select v-model="searchForm.status" placeholder="审核状态" clearable>
            <el-option label="全部" value="" />
            <el-option label="待审核" value="PENDING" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
            <el-option label="已禁用" value="DISABLED" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="searchForm.community" placeholder="所属社区" clearable>
            <el-option label="全部" value="" />
            <el-option label="未分配" value="unassigned" />
            <el-option
              v-for="community in communityList"
              :key="community.id"
              :label="community.name"
              :value="community.id"
            />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetSearch">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-col>
      </el-row>
    </el-card>


    <!-- 团长列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>团长列表</span>
          <div class="header-actions">
            <el-button type="success" @click="batchApprove" :disabled="selectedLeaders.length === 0">
              <el-icon><Check /></el-icon>
              批量通过
            </el-button>
            <el-button type="danger" @click="batchReject" :disabled="selectedLeaders.length === 0">
              <el-icon><Close /></el-icon>
              批量拒绝
            </el-button>
            <el-button type="primary" @click="showCommunityAssign">
              <el-icon><Location /></el-icon>
              社区分配
            </el-button>
            
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="leaderList"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :size="50" :src="row.avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" width="180" show-overflow-tooltip />
        <el-table-column label="所属社区" width="150">
          <template #default="{ row }">
            <el-tag v-if="row.communityName" type="success">{{ row.communityName }}</el-tag>
            <el-tag v-else type="info">未分配</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="管理用户" width="100">
          <template #default="{ row }">
            <el-tag type="primary">{{ row.memberCount || 0 }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="本月订单" width="100">
          <template #default="{ row }">
            <el-tag type="warning">{{ row.monthlyOrders || 0 }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="本月收益" width="120">
          <template #default="{ row }">
            <span class="revenue">¥{{ (row.monthlyRevenue || 0).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="审核状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="160" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="viewLeader(row)"
            >
              查看
            </el-button>
            <el-button
              v-if="row.status === 'PENDING'"
              type="success"
              size="small"
              @click="approveLeader(row)"
            >
              通过
            </el-button>
            <el-button
              v-if="row.status === 'PENDING'"
              type="danger"
              size="small"
              @click="rejectLeader(row)"
            >
              拒绝
            </el-button>
            <el-button
              v-if="row.status === 'APPROVED' && !row.communityId"
              type="info"
              size="small"
              @click="assignCommunity(row)"
            >
              分配社区
            </el-button>
            <el-button
              v-if="row.status === 'APPROVED' && row.communityId"
              type="warning"
              size="small"
              @click="unassignCommunity(row)"
            >
              取消分配
            </el-button>
            <el-button
              v-if="row.status === 'APPROVED' && row.communityId"
              type="primary"
              size="small"
              @click="assignCommunity(row)"
            >
              重新分配
            </el-button>
            
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 团长详情对话框 -->
    <el-dialog
      v-model="leaderDetailVisible"
      title="团长详情"
      width="900px"
      :before-close="closeLeaderDetail"
    >
      <div v-if="currentLeader" class="leader-detail">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="基本信息" name="basic">
            <el-row :gutter="20">
              <el-col :span="6">
                <div class="avatar-section">
                  <el-avatar :size="120" :src="currentLeader.avatar">
                    <el-icon><User /></el-icon>
                  </el-avatar>
                </div>
              </el-col>
              <el-col :span="18">
                <el-descriptions :column="2" border>
                  <el-descriptions-item label="团长ID">{{ currentLeader.id }}</el-descriptions-item>
                  <el-descriptions-item label="姓名">{{ currentLeader.realName }}</el-descriptions-item>
                  <el-descriptions-item label="手机号">{{ currentLeader.phone }}</el-descriptions-item>
                  <el-descriptions-item label="邮箱">{{ currentLeader.email }}</el-descriptions-item>
                  <el-descriptions-item label="身份证号">{{ currentLeader.idCard || '未提供' }}</el-descriptions-item>
                  <el-descriptions-item label="所属社区">
                    <el-tag v-if="currentLeader.communityName" type="success">{{ currentLeader.communityName }}</el-tag>
                    <el-tag v-else type="info">未分配</el-tag>
                  </el-descriptions-item>
                  <el-descriptions-item label="审核状态">
                    <el-tag :type="getStatusTagType(currentLeader.status)">{{ getStatusText(currentLeader.status) }}</el-tag>
                  </el-descriptions-item>
                  <el-descriptions-item label="管理用户数">{{ currentLeader.memberCount || 0 }}</el-descriptions-item>
                  <el-descriptions-item label="申请时间">{{ currentLeader.createTime }}</el-descriptions-item>
                  <el-descriptions-item label="审核时间">{{ currentLeader.auditTime || '未审核' }}</el-descriptions-item>
                  <el-descriptions-item label="详细地址" :span="2">
                    {{ currentLeader.address || '未提供' }}
                  </el-descriptions-item>
                  <el-descriptions-item label="申请理由" :span="2">
                    <div class="apply-reason">{{ currentLeader.applyReason || '暂无' }}</div>
                  </el-descriptions-item>
                </el-descriptions>
              </el-col>
            </el-row>
          </el-tab-pane>
          
          <el-tab-pane label="业绩统计" name="performance">
            <el-row :gutter="20">
              <el-col :span="6">
                <el-statistic title="本月订单" :value="currentLeader.monthlyOrders || 0" />
              </el-col>
              <el-col :span="6">
                <el-statistic title="本月收益" :value="currentLeader.monthlyRevenue || 0" precision="2" prefix="¥" />
              </el-col>
              <el-col :span="6">
                <el-statistic title="累计订单" :value="currentLeader.totalOrders || 0" />
              </el-col>
              <el-col :span="6">
                <el-statistic title="累计收益" :value="currentLeader.totalRevenue || 0" precision="2" prefix="¥" />
              </el-col>
            </el-row>
            
            <div class="performance-chart" style="margin-top: 30px;">
              <h4>近6个月业绩趋势</h4>
              <div class="chart-placeholder">
                <el-empty description="图表功能开发中" />
              </div>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="管理用户" name="members">
            <el-table :data="currentLeader.members || []" style="width: 100%">
              <el-table-column prop="id" label="用户ID" width="80" />
              <el-table-column prop="username" label="用户名" width="120" />
              <el-table-column prop="realName" label="真实姓名" width="100" />
              <el-table-column prop="phone" label="手机号" width="130" />
              <el-table-column prop="orderCount" label="订单数" width="80" />
              <el-table-column prop="totalAmount" label="消费金额" width="100">
                <template #default="{ row }">
                  ¥{{ (row.totalAmount || 0).toFixed(2) }}
                </template>
              </el-table-column>
              <el-table-column prop="joinTime" label="加入时间" width="160" />
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeLeaderDetail">关闭</el-button>
          <el-button
            v-if="currentLeader?.status === 'PENDING'"
            type="success"
            @click="approveLeader(currentLeader)"
          >
            通过审核
          </el-button>
          <el-button
            v-if="currentLeader?.status === 'PENDING'"
            type="danger"
            @click="rejectLeader(currentLeader)"
          >
            拒绝审核
          </el-button>
          <el-button
            v-if="currentLeader?.status === 'APPROVED'"
            type="primary"
            @click="assignCommunity(currentLeader)"
          >
            分配社区
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 社区分配对话框 -->
    <el-dialog
      v-model="communityAssignVisible"
      :title="assignForm.currentCommunity === '未分配' ? '社区分配' : '重新分配社区'"
      width="600px"
    >
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="团长">
          <el-input v-model="assignForm.realName" disabled />
        </el-form-item>
        <el-form-item label="当前社区">
          <el-input v-model="assignForm.currentCommunity" disabled />
        </el-form-item>
        <el-form-item label="选择社区" required>
          <el-select 
            v-model="assignForm.communityId" 
            placeholder="请选择社区" 
            style="width: 100%"
            filterable
            clearable
          >
            <el-option
              v-for="community in getAvailableCommunitiesForAssign()"
              :key="community.id"
              :label="`${community.name} (${community.address})`"
              :value="community.id"
            >
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <span>{{ community.name }}</span>
                <span style="color: #8492a6; font-size: 12px;">{{ community.address }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="分配说明">
          <el-alert
            v-if="assignForm.currentCommunity !== '未分配'"
            title="重新分配将会取消当前社区的分配关系"
            type="warning"
            :closable="false"
            style="margin-bottom: 10px;"
          />
          <el-text type="info" size="small">
            {{ availableCommunities.length === 0 ? '暂无可分配的社区' : `共有 ${getAvailableCommunitiesForAssign().length} 个社区可供分配` }}
          </el-text>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="assignForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入分配备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="communityAssignVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="confirmAssign"
            :disabled="!assignForm.communityId"
          >
            {{ assignForm.currentCommunity === '未分配' ? '确定分配' : '确定重新分配' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Download,
  Clock,
  Check,
  Close,
  User,
  UserFilled,
  Location
} from '@element-plus/icons-vue'
import {
  getGroupLeaderList,
  approveGroupLeader,
  rejectGroupLeader,
  updateGroupLeaderStatus,
  getGroupLeaderStats,
  assignCommunityToLeader,
  getCommunityList
} from '@/api/admin'

// 响应式数据
const loading = ref(false)
const leaderList = ref([])
const selectedLeaders = ref([])
const leaderDetailVisible = ref(false)
const communityAssignVisible = ref(false)
const currentLeader = ref(null)
const activeTab = ref('basic')
const communityList = ref([])
const availableCommunities = ref([])

// 统计数据
const stats = reactive({
  pending: 0,
  approved: 0,
  active: 0,
  total: 0
})

// 搜索表单
const searchForm = reactive({
  keyword: '',
  status: '',
  community: '',
  dateRange: []
})

// 分页
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 社区分配表单
const assignForm = reactive({
  leaderId: '',
  realName: '',
  communityId: '',
  currentCommunity: '',
  remark: ''
})

// 获取团长列表
const loadLeaderList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword,
      status: searchForm.status,
      startDate: searchForm.dateRange?.[0],
      endDate: searchForm.dateRange?.[1]
    }
    
    // 处理社区筛选
    if (searchForm.community) {
      if (searchForm.community === 'unassigned') {
        params.unassigned = true
      } else {
        params.communityId = searchForm.community
      }
    }
    
    const response = await getGroupLeaderList(params)
    console.log('团长列表响应:', response)
    // 管理员API返回的数据结构是 {data: {records, total}}
    const data = response.data || response
    leaderList.value = data.records || []
    pagination.total = data.total || 0
  } catch (error) {
    ElMessage.error('获取团长列表失败')
  } finally {
    loading.value = false
  }
}

// 获取统计数据
const loadStats = async () => {
  try {
    const response = await getGroupLeaderStats()
    Object.assign(stats, response)
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取社区列表
const loadCommunityList = async () => {
  try {
    const response = await getCommunityList()
    communityList.value = response.data
    availableCommunities.value = response.data.filter(c => !c.leaderId)
  } catch (error) {
    console.error('获取社区列表失败:', error)
  }
}

// 获取可分配的社区列表（包含当前团长已分配的社区）
const getAvailableCommunitiesForAssign = () => {
  if (!assignForm.leaderId) return availableCommunities.value
  
  // 如果团长已经分配了社区，则包含当前分配的社区
  const currentLeaderCommunity = communityList.value.find(c => c.id === assignForm.communityId)
  const available = [...availableCommunities.value]
  
  if (currentLeaderCommunity && !available.find(c => c.id === currentLeaderCommunity.id)) {
    available.unshift(currentLeaderCommunity)
  }
  
  return available
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadLeaderList()
}

// 重置搜索
const resetSearch = () => {
  Object.assign(searchForm, {
    keyword: '',
    status: '',
    community: '',
    dateRange: []
  })
  pagination.page = 1
  loadLeaderList()
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadLeaderList()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadLeaderList()
}

// 选择处理
const handleSelectionChange = (selection) => {
  selectedLeaders.value = selection
}

// 查看团长详情
const viewLeader = (leader) => {
  currentLeader.value = leader
  activeTab.value = 'basic'
  leaderDetailVisible.value = true
}

// 关闭团长详情
const closeLeaderDetail = () => {
  leaderDetailVisible.value = false
  currentLeader.value = null
}

// 审核通过
const approveLeader = async (leader) => {
  try {
    await ElMessageBox.confirm(`确定要通过团长 "${leader.realName}" 的审核吗？`, '确认审核', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })
    
    await approveGroupLeader(leader.id)
    ElMessage.success('审核通过')
    loadLeaderList()
    loadStats()
    closeLeaderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 审核拒绝
const rejectLeader = async (leader) => {
  try {
    const { value: reason } = await ElMessageBox.prompt(
      `请输入拒绝团长 "${leader.realName}" 的原因：`,
      '拒绝审核',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '请输入拒绝原因',
        inputValidator: (value) => {
          if (!value || value.trim() === '') {
            return '请输入拒绝原因'
          }
          return true
        }
      }
    )
    
    await rejectGroupLeader(leader.id, reason)
    ElMessage.success('已拒绝审核')
    loadLeaderList()
    loadStats()
    closeLeaderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}



// 分配社区
const assignCommunity = (leader) => {
  assignForm.leaderId = leader.id
  assignForm.realName = leader.realName
  assignForm.communityId = leader.communityId || ''
  assignForm.currentCommunity = leader.communityName || '未分配'
  assignForm.remark = ''
  communityAssignVisible.value = true
}

// 确认分配社区
const confirmAssign = async () => {
  if (!assignForm.communityId) {
    ElMessage.warning('请选择社区')
    return
  }
  
  try {
    await assignCommunityToLeader(assignForm.leaderId, assignForm.communityId)
    
    ElMessage.success('社区分配成功')
    communityAssignVisible.value = false
    loadLeaderList()
    loadCommunityList()
  } catch (error) {
    ElMessage.error('分配失败')
  }
}

// 显示社区分配（批量操作）
const showCommunityAssign = () => {
  const approvedLeaders = selectedLeaders.value.filter(l => l.status === 'APPROVED' && !l.communityId)
  if (approvedLeaders.length === 0) {
    ElMessage.warning('请选择已审核通过且未分配社区的团长')
    return
  }
  
  if (approvedLeaders.length === 1) {
    assignCommunity(approvedLeaders[0])
  } else {
    ElMessage.info(`已选择 ${approvedLeaders.length} 个团长，请逐个进行社区分配`)
    // 可以考虑实现批量分配功能
    assignCommunity(approvedLeaders[0])
  }
}

// 取消社区分配
const unassignCommunity = async (leader) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消团长 ${leader.realName} 的社区分配吗？`,
      '取消分配',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await assignCommunityToLeader(leader.id, null)
    ElMessage.success('取消分配成功')
    loadLeaderList()
    loadCommunityList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消分配失败')
    }
  }
}

// 批量审核通过
const batchApprove = async () => {
  const pendingLeaders = selectedLeaders.value.filter(l => l.status === 'PENDING')
  if (pendingLeaders.length === 0) {
    ElMessage.warning('请选择待审核的团长')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要批量通过 ${pendingLeaders.length} 个团长的审核吗？`, '批量审核', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })
    
    for (const leader of pendingLeaders) {
      await approveGroupLeader(leader.id)
    }
    
    ElMessage.success('批量审核通过成功')
    loadLeaderList()
    loadStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量操作失败')
    }
  }
}

// 批量审核拒绝
const batchReject = async () => {
  const pendingLeaders = selectedLeaders.value.filter(l => l.status === 'PENDING')
  if (pendingLeaders.length === 0) {
    ElMessage.warning('请选择待审核的团长')
    return
  }
  
  try {
    const { value: reason } = await ElMessageBox.prompt(
      `请输入批量拒绝 ${pendingLeaders.length} 个团长的原因：`,
      '批量拒绝审核',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '请输入拒绝原因',
        inputValidator: (value) => {
          if (!value || value.trim() === '') {
            return '请输入拒绝原因'
          }
          return true
        }
      }
    )
    
    for (const leader of pendingLeaders) {
      await rejectGroupLeader(leader.id, reason)
    }
    
    ElMessage.success('批量拒绝成功')
    loadLeaderList()
    loadStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量操作失败')
    }
  }
}


// 工具函数
const getStatusText = (status) => {
  const statusMap = {
    'PENDING': '待审核',
    'APPROVED': '已通过',
    'REJECTED': '已拒绝',
    'DISABLED': '已禁用'
  }
  return statusMap[status] || status
}

const getStatusTagType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger',
    'DISABLED': 'info'
  }
  return typeMap[status] || ''
}

// 初始化
onMounted(() => {
  loadLeaderList()
  loadStats()
  loadCommunityList()
})
</script>

<style scoped>
.group-leader-management {
  width: 100%;
  max-width: none;
}

.page-header {
  margin-bottom: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.page-header :deep(.el-card__body) {
  padding: 30px;
}

.header-content h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
}

.header-content p {
  margin: 0;
  opacity: 0.9;
  font-size: 14px;
}

.search-card {
  margin-bottom: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 8px;
  overflow: hidden;
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 24px;
  color: white;
}

.stat-icon.pending {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.approved {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.active {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-icon.total {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-icon.assigned {
  background: linear-gradient(135deg, #722ed1, #9254de);
}

.stat-icon.unassigned {
  background: linear-gradient(135deg, #fa8c16, #ffa940);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.table-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.leader-detail {
  padding: 20px 0;
}

.avatar-section {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.apply-reason {
  max-height: 100px;
  overflow-y: auto;
  line-height: 1.6;
}

.performance-chart {
  text-align: center;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.revenue {
  color: #67c23a;
  font-weight: 600;
}

/* 表格样式优化 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table__header) {
  background-color: #f8f9fa;
}

:deep(.el-table th) {
  background-color: #f8f9fa;
  color: #606266;
  font-weight: 600;
}

:deep(.el-table td) {
  border-bottom: 1px solid #f0f0f0;
}

:deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}

/* 按钮样式 */
.el-button + .el-button {
  margin-left: 8px;
}

/* 标签样式 */
.el-tag {
  border-radius: 12px;
  padding: 0 8px;
  font-size: 12px;
}

/* 头像样式 */
.el-avatar {
  border: 2px solid #f0f0f0;
}

/* 统计组件样式 */
:deep(.el-statistic__content) {
  font-size: 24px;
  font-weight: 600;
}

:deep(.el-statistic__head) {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}
</style>