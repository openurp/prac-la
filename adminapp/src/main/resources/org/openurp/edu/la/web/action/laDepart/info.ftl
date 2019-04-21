[#ftl]
[@b.head/]
[@b.toolbar title="项目单位信息"]
  bar.addBack("${b.text("action.back")}");
[/@]
<table class="infoTable">
  <tr>
    <td class="title" width="20%">单位名称</td>
    <td class="content">${laDepart.name}</td>
  </tr>
  <tr>
    <td class="title" width="20%">是否开放</td>
    <td class="content">${laDepart.opened?string('是','否') }</td>
  </tr>
  <tr>
    <td class="title" width="20%">要求</td>
    <td class="content" >${laDepart.request!}</td>
  </tr>
  <tr>
    <td class="title" width="20%">福利条件</td>
    <td class="content" >${laDepart.benefits!}</td>
  </tr>
  <tr>
    <td class="title" width="20%">人数上限</td>
    <td class="content" >${laDepart.capacity}</td>
  </tr>
</table>

[@b.foot/]
