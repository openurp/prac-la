[#ftl]
[@b.head/]
[@b.toolbar title="项目单位信息"]
  bar.addBack("${b.text("action.back")}");
[/@]
<table class="infoTable">
  <tr>
    <td class="title" width="20%">单位名称</td>
    <td class="content">${laOption.corporation.name}</td>
  </tr>
  <tr>
    <td class="title" width="20%">批次</td>
    <td class="content">${laOption.semester.schoolYear}学年度${laOption.semester.name}学期 ${laOption.session.name}</td>
  </tr>
  <tr>
    <td class="title" width="20%">要求</td>
    <td class="content" >${laOption.requirement!}</td>
  </tr>
  <tr>
    <td class="title" width="20%">备注</td>
    <td class="content" >${laOption.remark!}</td>
  </tr>
  <tr>
    <td class="title" width="20%">报名人数上限</td>
    <td class="content" >${laOption.capacity}</td>
  </tr>
  <tr>
    <td class="title" width="20%">报名实际人数</td>
    <td class="content" >${laOption.takers?size}</td>
  </tr>
  <tr>
    <td class="title" width="20%">录取人数上限</td>
    <td class="content" >${laOption.enrollLimit}</td>
  </tr>
  <tr>
    <td class="title" width="20%">录取实际人数</td>
    <td class="content" >${laOption.volunteers?size}</td>
  </tr>
</table>

[@b.foot/]