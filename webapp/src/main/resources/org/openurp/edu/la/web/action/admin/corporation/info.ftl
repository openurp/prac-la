[#ftl]
[@b.head/]
[@b.toolbar title="企业信息"]
  bar.addBack("${b.text("action.back")}");
[/@]
<table class="infoTable">
  <tr>
    <td class="title" width="20%">单位名称</td>
    <td class="content">${corporation.name}</td>
  </tr>
  <tr>
    <td class="title" width="20%">要求</td>
    <td class="content" >${corporation.request!}</td>
  </tr>
  <tr>
    <td class="title" width="20%">福利条件</td>
    <td class="content" >${corporation.benefits!}</td>
  </tr>
</table>

[@b.foot/]
