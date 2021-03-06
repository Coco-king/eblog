<#include "/include/layout.ftl" />

<@layout "管理中心">

    <div class="layui-container fly-marginTop fly-user-main">

        <@userCenterLeft index=5></@userCenterLeft>

        <div class="site-tree-mobile layui-hide">
            <i class="layui-icon">&#xe602;</i>
        </div>
        <div class="site-mobile-shade"></div>

        <div class="site-tree-mobile layui-hide">
            <i class="layui-icon">&#xe602;</i>
        </div>
        <div class="site-mobile-shade"></div>


        <div class="fly-panel fly-panel-user" pad20>
            <div class="layui-tab layui-tab-brief" lay-filter="user">
                <ul class="layui-tab-title" id="LAY_mine">
                    <li data-type="mine-jie" lay-id="index" class="layui-this">
                        未审核的帖子（<span id="fabuCount"></span>）
                    </li>
                    <li data-type="collection" data-url="/collection/find" lay-id="collection">
                        我收藏的帖（<span id="collectionCount"></span>）
                    </li>
                </ul>
                <div class="layui-tab-content" style="padding: 20px 0;">
                    <div class="layui-tab-item layui-show">
                        <ul class="mine-view jie-row" id="fabu">
                            <script id="tpl-fabu" type="text/html">
                                <li>
                                    <a class="jie-title" href="/post/{{ d.id }}" target="_blank">{{ d.title }}</a>
                                    <i>{{ layui.util.toDateString(d.created) }}</i>
                                    <a class="mine-edit" href="/post/edit?id={{ d.id }}">编辑</a>
                                    <a class="mine-edit" href="/admin/pass?id={{ d.id }}"
                                       style="height: 20px;margin-left: 5px;background-color: #23551f">
                                        通过
                                    </a>
                                    <a class="mine-edit" href="/admin/delete?id={{ d.id }}"
                                       style="height: 20px;margin-left: 5px;background-color: #393d49">
                                        删除
                                    </a>
                                </li>
                            </script>
                        </ul>
                        <div id="LAY_page"></div>
                    </div>
                    <div class="layui-tab-item">
                        <ul class="mine-view jie-row" id="collection">
                            <script id="tpl-collection" type="text/html">
                                <li>
                                    <a class="jie-title" href="/post/{{ d.id }}" target="_blank">{{ d.title }}</a>
                                    <i>收藏于{{ layui.util.timeAgo(d.collectionCreated,true) }}</i>
                                </li>
                            </script>
                        </ul>
                        <div id="LAY_page1"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
      layui.cache.page = 'user';

      layui.use(['laytpl', 'util', 'flow'], function () {
        var laytpl = layui.laytpl;
        var util = layui.util;
        var flow = layui.flow;
        var $ = layui.jquery;

        flow.load({
          elem: '#fabu' //指定列表容器
          , done: function (page, next) { //到达临界点（默认滚动触发），触发下一页
            var lis = [];
            //以jQuery的Ajax请求为例，请求下一页数据（注意：page是从2开始返回）
            $.post('/admin/check/noCheck?cp=' + page, function (res) {
              $("#fabuCount").html(res.data.total);
              //假设你的列表返回在data集合中
              layui.each(res.data.records, function (index, item) {
                //使用模板引擎渲染
                var getTpl = $('#tpl-fabu').html();
                laytpl(getTpl).render(item, function (html) {
                  //在layui-flow-more这个div前面填充元素
                  $('#fabu .layui-flow-more').before(html);
                  // lis.push(html);
                });
              });
              //执行下一页渲染，第二参数为：满足“加载更多”的条件，即后面仍有分页
              //pages为Ajax返回的总页数，只有当前页小于总页数的情况下，才会继续出现加载更多
              next(lis.join(''), page < res.data.pages);
            });
          }
        });

        flow.load({
          elem: '#collection' //指定列表容器
          , done: function (page, next) { //到达临界点（默认滚动触发），触发下一页
            var lis = [];
            //以jQuery的Ajax请求为例，请求下一页数据（注意：page是从2开始返回）
            $.get('/user/collection?cp=' + page, function (res) {
              $("#collectionCount").html(res.data.total);
              //假设你的列表返回在data集合中
              layui.each(res.data.records, function (index, item) {
                //使用模板引擎渲染
                var getTpl = $('#tpl-collection').html();
                laytpl(getTpl).render(item, function (html) {
                  //在layui-flow-more这个div前面填充元素
                  $('#collection .layui-flow-more').before(html);
                  // lis.push(html);
                });
              });
              //执行下一页渲染，第二参数为：满足“加载更多”的条件，即后面仍有分页
              //pages为Ajax返回的总页数，只有当前页小于总页数的情况下，才会继续出现加载更多
              next(lis.join(''), page < res.data.pages);
            });
          }
        });
      });

    </script>
</@layout>
