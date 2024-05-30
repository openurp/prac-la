[#ftl]
[@b.head/]
[@b.toolbar title="项目单位信息"]
  bar.addBack("${b.text("action.back")}");
[/@]
<table class="infoTable">
  <tr>
    <td class="title" width="20%">批次</td>
    <td class="content">${laOption.semester.schoolYear}学年度${laOption.semester.name}学期 ${laOption.session.name}</td>
    <td class="title" width="20%">单位名称</td>
    <td class="content">${laOption.corporation.name}</td>
  </tr>
  <tr>
    <td class="title" width="20%">要求</td>
    <td class="content" >${laOption.requirement!}</td>
    <td class="title" width="20%">备注</td>
    <td class="content" >${laOption.remark!}</td>
  </tr>
  <tr>
    <td class="title" width="20%">报名人数上限</td>
    <td class="content" >${laOption.capacity}</td>
    <td class="title" width="20%">报名实际人数</td>
    <td class="content" >${laOption.takers?size}</td>
  </tr>
  <tr>
    <td class="title" width="20%">录取人数上限</td>
    <td class="content" >${laOption.enrollLimit}</td>
    <td class="title" width="20%">录取实际人数</td>
    <td class="content" >${laOption.volunteers?size}</td>
  </tr>
</table>
[@b.grid items=laOption.takers?sort_by("rank") var="laTaker"]
  [@b.row]
      [@b.col title="序号" width="4%"]${laTaker_index+1}[/@]
      [@b.col property="rank" title="志愿" width="4%"/]
      [@b.col property="volunteer.std.code" title="学号" width="10%"/]
      [@b.col property="volunteer.std.name" title="姓名" width="9%"]<span title="${laTaker.updatedAt?string('MM-dd HH:mm')}报名,${laTaker.volunteer.mobile}">${laTaker.volunteer.std.name}</span>[/@]
      [@b.col property="volunteer.std.state.grade" title="年级" width="7%"/]
      [@b.col property="volunteer.std.state.department.name" title="院系" width="15%"/]
      [@b.col property="volunteer.std.state.major.name" title="专业" width="15%"]${(laTaker.volunteer.std.state.major.name)!}[/@]
      [@b.col title="录取单位" width="20%"]
        ${(laTaker.volunteer.enrolledRank)!} ${(laTaker.volunteer.enrolledOption.corporation.name)!}
      [/@]
      [@b.col property="volunteer.gpa" title="平均绩点" width="6%"/]
      [@b.col property="updatedAt" title="报名时间" width="10%"]
        ${laTaker.volunteer.updatedAt?string('MM-dd HH:mm')}
      [/@]
  [/@]
[/@]
[@b.foot/]
