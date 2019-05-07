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
    <td class="content" >${laOption.actual}</td>
  </tr>
  <tr>
    <td class="title" width="20%">录取人数上限</td>
    <td class="content" >${laOption.enrollLimit}</td>
  </tr>
  <tr>
    <td class="title" width="20%">录取实际人数</td>
    <td class="content" >${laOption.enrolled}</td>
  </tr>
</table>

[@b.foot/]
