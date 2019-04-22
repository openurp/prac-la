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
    <td class="content" >${laOption.request!}</td>
  </tr>
  <tr>
    <td class="title" width="20%">福利条件</td>
    <td class="content" >${laOption.benefits!}</td>
  </tr>
  <tr>
    <td class="title" width="20%">人数上限</td>
    <td class="content" >${laOption.capacity}</td>
  </tr>
  <tr>
    <td class="title" width="20%">实际人数</td>
    <td class="content" >${laOption.actual}</td>
  </tr>
</table>

[@b.foot/]
